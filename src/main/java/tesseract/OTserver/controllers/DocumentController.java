package tesseract.OTserver.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.exceptions.DocumentNotFoundException;
import tesseract.OTserver.objects.GetDocumentMetaResponse;
import tesseract.OTserver.objects.OpenDocumentRequest;
import tesseract.OTserver.objects.CreateDocumentRequest;
import tesseract.OTserver.objects.GetDocumentResponse;
import tesseract.OTserver.services.DocumentService;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "/{id}"
    )
    @ResponseBody
    public GetDocumentResponse getDocument(HttpServletRequest httpRequest, @PathVariable Long id, @RequestParam String password) throws IOException {
        logger.info("Document [{}] was requested.", id);
        return documentService.getDocumentById(id, password);
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "meta/{id}"
    )
    @ResponseBody
    public GetDocumentMetaResponse getDocument(HttpServletRequest httpRequest, @PathVariable Long id) throws IOException {
        logger.info("Document meta [{}] was requested.", id);
        return documentService.getDocumentById(id);
    }

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST
    )
    @ResponseBody
    public Long createDocument(HttpServletRequest httpRequest, @RequestBody CreateDocumentRequest request) throws IOException {
        logger.info("Created document was requested {}.", request);
        return documentService.createDocument(request);
    }

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "/open"
    )
    @ResponseBody
    public void openAndAuthenticateDocument(HttpServletRequest httpRequest, @RequestBody OpenDocumentRequest request) throws IOException {
        logger.info("Request to open document [{}].", request.getId());
        documentService.openAndAuthenticateDocument(request);
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.PUT,
            path = "/{id}"
    )
    @ResponseBody
    public void saveDocument(HttpServletRequest httpRequest, @PathVariable Long id, @RequestParam String password) throws IOException {
        logger.info("Save document [{}] was requested.", id);
        documentService.saveDocumentModel(id, password);
    }
}