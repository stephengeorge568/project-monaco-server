package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class DocumentAlreadyOpenException extends RuntimeException {
    public DocumentAlreadyOpenException(Long id) {
        super("Document with id " + id + " is already in memory.");
    }
    public DocumentAlreadyOpenException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentAlreadyOpenException(String message) {
        super(message);
    }
    public DocumentAlreadyOpenException(Throwable cause) {
        super(cause);
    }
}