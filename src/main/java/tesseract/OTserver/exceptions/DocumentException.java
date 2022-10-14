
package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DocumentException extends RuntimeException {
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentException(String message) {
        super(message);
    }
    public DocumentException(Throwable cause) {
        super(cause);
    }
}