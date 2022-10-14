package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class DocumentNotInMemoryException extends RuntimeException {
    public DocumentNotInMemoryException(Long id) {
        super("Document " + id + " is not opened in memory. Use /api/document/open endpoint to open document.");
    }
    public DocumentNotInMemoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentNotInMemoryException(String message) {
        super(message);
    }
    public DocumentNotInMemoryException(Throwable cause) {
        super(cause);
    }
}
