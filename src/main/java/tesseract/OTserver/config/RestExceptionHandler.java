package tesseract.OTserver.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tesseract.OTserver.controllers.DocumentController;
import tesseract.OTserver.exceptions.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(value = DocumentNotFoundException.class)
    public ResponseEntity handlerDocumentException(DocumentNotFoundException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DocumentNotFoundInFilesystemException.class)
    public ResponseEntity handlerDocumentException(DocumentNotFoundInFilesystemException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DocumentAlreadyOpenException.class)
    public ResponseEntity handlerDocumentException(DocumentAlreadyOpenException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(value = DocumentWrongPasswordException.class)
    public ResponseEntity handlerDocumentException(DocumentWrongPasswordException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = DocumentNotInMemoryException.class)
    public ResponseEntity handlerDocumentException(DocumentNotInMemoryException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = DocumentException.class)
    public ResponseEntity handlerDocumentException(DocumentException e) {
        logger.error("", e);
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
