package tesseract.OTserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tesseract.OTserver.objects.Document;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.util.DocumentUtil;
import tesseract.OTserver.util.OperationalTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Service
public class OtService {


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate; // for websocket messaging

    // have it create new document when client connects TODO
    private Document currentDocument;

    // Id given to each client that connections to this document session. This value is incremented.\
    // Temporary, 'sticky' session management will replace this
    private Integer clientIdentityCounter;

    public OtService() {
        this.clientIdentityCounter = 0;
        this.currentDocument = new Document();
    }

    /**
     * Submit string change request to document. Transforms request and commits it to change history.
     * @param request the request to transform and commit to change history
     * @param document the document to submit change to
     * @return the new document revID
     */
    public Integer submitChange(StringChangeRequest request, Document document) {

        document.getPendingChangesQueue().add(request);
        waitForTurn(request);

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

                updateModel(changedRequest);
                propogateToClients(changedRequest);
            }
        }

        // remove this request from pending queue, since it is completed
        this.currentDocument.getPendingChangesQueue().remove();

        // return revID so client can update its document id
        return this.currentDocument.getRevID();
    }

    /**
     * Makes THIS request's thread wait for other, preceding threads to finish transforming
     * TODO Replace with a FIFO blocking queue or something like that
     * @param request the request this thread is responsible for
     */
    private void waitForTurn(StringChangeRequest request) {
        // while the next request in queue is not THIS one, wait 10 ms
        while (!(this.currentDocument.getPendingChangesQueue().peek().getTimestamp().equals(request.getTimestamp())
                && this.currentDocument.getPendingChangesQueue().peek().getIdentity().equals(request.getIdentity()))) {
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
        this.currentDocument.setModel(DocumentUtil.updateModel(this.currentDocument.getModel(), changedRequest));
    }

    /**
     * Send request to clients. Sets the set ID to the document's revID
     * @param changedRequest the request to propogate to clients
     */
    private void propogateToClients(StringChangeRequest changedRequest) {
        changedRequest.setSetID(this.currentDocument.getRevID());
        this.simpMessagingTemplate.convertAndSend("/broker/string-change-request", changedRequest);
    }

    public String getDocumentModel(Long id) {
        return this.currentDocument.getModel();
    }

    public Integer getDocumentRevId() {
        return this.currentDocument.getRevID();
    }

    public Integer getClientIdentityCounter() {
        return clientIdentityCounter;
    }

    public void incrementClientIdentityCounter() {
        this.clientIdentityCounter++;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }
}
