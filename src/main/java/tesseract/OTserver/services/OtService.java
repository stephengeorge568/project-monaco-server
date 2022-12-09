package tesseract.OTserver.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tesseract.OTserver.exceptions.DocumentNotInMemoryException;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.util.DocumentUtil;
import tesseract.OTserver.util.OperationalTransformation;

import java.io.IOException;
import java.util.*;

@Service
public class OtService {

    private static final Logger logger = LogManager.getLogger(OtService.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // for websocket messaging

    // Id given to each client that connections to this document session. This value is incremented.\
    // Temporary, 'sticky' session management will replace this
    private Integer clientIdentityCounter;

    private HashMap<Long, Document> documents;

    public OtService() {
        this.documents = new HashMap<>();
        this.clientIdentityCounter = 0;
    }

    public Document getDocument(Long id) throws IOException {
        if (!this.documents.containsKey(id)) throw new DocumentNotInMemoryException(id);
        return this.documents.get(id);
    }

    /**
     * Submit string change request to document. Transforms request and commits it to change history.
     * @param request the request to transform and commit to change history
     * @param document the document to submit change to
     * @return the new document revID
     */
    public Integer submitChange(StringChangeRequest request, Document document) {

        document.getPendingChangesQueue().add(request);
        waitForTurn(request, document);

        // when this request's turn is next, transform
        ArrayList<StringChangeRequest> newChangeRequests = OperationalTransformation.transform(request, document.getChangeHistory());

        // increment document id
        document.setRevID(document.getRevID() + 1);

        // transform can return multiple string change requests, so iterate over each
        for (StringChangeRequest changedRequest : newChangeRequests) {
            if (changedRequest != null) {
                if (document.getChangeHistory().get(changedRequest.getRevID()) != null)
                    document.getChangeHistory().get(changedRequest.getRevID()).add(changedRequest);
                else
                    document.getChangeHistory().put(changedRequest.getRevID(), new ArrayList<>(Arrays.asList(changedRequest)));
                logger.info("SCR T: {}", request.toString());
                logger.info("\n");
                updateModel(changedRequest);
                document.setHasChanged(true);
                propogateToClients(changedRequest);
            }
        }

        // remove this request from pending queue, since it is completed
        document.getPendingChangesQueue().remove();

        // return revID so client can update its document id
        return document.getRevID();
    }

    /**
     * Makes THIS request's thread wait for other, preceding threads to finish transforming
     * TODO Replace with a FIFO blocking queue or something like that
     * @param request the request this thread is responsible for
     */
    private void waitForTurn(StringChangeRequest request, Document document) {
        // while the next request in queue is not THIS one, wait 10 ms
        while (!(document.getPendingChangesQueue().peek().getTimestamp().equals(request.getTimestamp())
                && document.getPendingChangesQueue().peek().getIdentity().equals(request.getIdentity()))) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the server side document string model
     * @param changedRequest the string change request
     */
    private void updateModel(StringChangeRequest changedRequest) {
        Document doc = this.getDocuments().get(changedRequest.getDocumentId());
        doc.setModel(DocumentUtil.updateModel(doc.getModel(), changedRequest));
    }

    /**
     * Send request to clients. Sets the set ID to the document's revID
     * @param changedRequest the request to propogate to clients
     */
    private void propogateToClients(StringChangeRequest changedRequest) {
        Document doc = this.getDocuments().get(changedRequest.getDocumentId());
        changedRequest.setSetID(doc.getRevID());
        this.simpMessagingTemplate.convertAndSend("/broker/" + doc.getId(), changedRequest);
    }

    public boolean isDocumentPresent(Long id) {
        return this.getDocuments().containsKey(id);
    }

    public String getDocumentModel(Long id) {
        return this.getDocuments().get(id).getModel();
    }

    public Integer getDocumentRevId(Long id) {
        return this.getDocuments().get(id).getRevID();
    }

    public Integer getClientIdentityCounter() {
        return clientIdentityCounter;
    }

    public void incrementClientIdentityCounter() {
        this.clientIdentityCounter++;
    }

    public HashMap<Long, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(HashMap<Long, Document> documents) {
        this.documents = documents;
    }
}
