package tesseract.OTserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.OpenDocumentRequest;
import tesseract.OTserver.objects.CreateDocumentRequest;
import tesseract.OTserver.objects.GetDocumentResponse;
import tesseract.OTserver.services.DocumentService;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "/{id}"
    )
    @ResponseBody
    public GetDocumentResponse getDocument(HttpServletRequest httpRequest, @PathVariable Long id, @RequestParam String password) throws IOException {
        return documentService.getDocumentById(id, password);
    }

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST
    )
    @ResponseBody
    public Long createDocument(HttpServletRequest httpRequest, @RequestBody CreateDocumentRequest request) throws IOException {
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
        documentService.openAndAuthenticateDocument(request);
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.PUT,
            path = "/{id}"
    )
    @ResponseBody
    public void saveDocument(HttpServletRequest httpRequest, @PathVariable Long id, @RequestParam String password) throws IOException {
        documentService.saveDocumentModel(id, password);
    }
}