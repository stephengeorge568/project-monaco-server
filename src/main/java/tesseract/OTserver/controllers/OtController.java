package tesseract.OTserver.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesseract.OTserver.objects.StringChangeRequest;
import tesseract.OTserver.services.OtService;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/ot")
public class OtController {

    private static final Logger logger = LogManager.getLogger(OtController.class);

    @Autowired
    private OtService otService;

    /**
     * Incoming string change requests from clients
     * @param httpRequest
     * @param request The request from the client
     * @return Response entity containing the id the client should update its revID to
     */
    @RequestMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            path = "/change"
    )
    @ResponseBody
    public ResponseEntity<Integer> stringChange(HttpServletRequest httpRequest, @RequestBody StringChangeRequest request) throws IOException {
        logger.info("SCR: {}", request.toString());
        return ResponseEntity.ok(this.otService.submitChange(request, this.otService.getDocument(request.getDocumentId())));
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "/identity"
    )
    @ResponseBody
    public ResponseEntity<Integer> getIdentity(HttpServletRequest httpRequest) {
        logger.info("{} has connected and requested user id.", httpRequest.getRemoteAddr());
        otService.incrementClientIdentityCounter();
        return ResponseEntity.ok(otService.getClientIdentityCounter());
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "/model/{id}"
    )
    @ResponseBody
    public String getModel(HttpServletRequest httpRequest, @PathVariable Long id) {
        logger.info("Model for document [{}] requested.", id);
        return otService.getDocumentModel(id);
    }

    @RequestMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            path = "/revId/{id}"
    )
    @ResponseBody
    public ResponseEntity<Integer> getDocumentRevID(HttpServletRequest httpRequest, @PathVariable Long id) {
        logger.info("Revision Id for document [{}] requested.", id);
        return ResponseEntity.ok(otService.getDocumentRevId(id));
    }

}