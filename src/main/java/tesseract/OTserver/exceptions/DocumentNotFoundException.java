package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(Long id) {
        super("Document with id " + id + " cannot be found in database.");
    }
    public DocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}