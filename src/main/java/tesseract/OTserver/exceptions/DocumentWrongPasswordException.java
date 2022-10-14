package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class DocumentWrongPasswordException extends RuntimeException {
    public DocumentWrongPasswordException(Long id) {
        super("Wrong password given for document " + id + ".");
    }
    public DocumentWrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentWrongPasswordException(String message) {
        super(message);
    }
    public DocumentWrongPasswordException(Throwable cause) {
        super(cause);
    }
}