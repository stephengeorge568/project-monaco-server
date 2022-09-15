package tesseract.OTserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.AuthenticateRequest;
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
    public GetDocumentResponse getDocument(HttpServletRequest httpRequest, @PathVariable Long id) throws IOException {
        return documentService.getDocumentById(id);
    }

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST
    )
    @ResponseBody
    public Long insertUserFriendRequest(HttpServletRequest httpRequest, @RequestBody CreateDocumentRequest request) {
        return documentService.createDocument(request);
    }

    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST
    )
    @ResponseBody
    public boolean authenticateDocumentPassword(HttpServletRequest httpRequest, @RequestBody AuthenticateRequest request) {
        return documentService.
    }


}