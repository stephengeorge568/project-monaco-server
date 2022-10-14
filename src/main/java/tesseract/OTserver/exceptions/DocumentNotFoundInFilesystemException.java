
package tesseract.OTserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DocumentNotFoundInFilesystemException extends RuntimeException {
    public DocumentNotFoundInFilesystemException(Long id) {
        super("Document with id " + id + " could not be found in the filesystem.");
    }
    public DocumentNotFoundInFilesystemException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentNotFoundInFilesystemException(String message) {
        super(message);
    }
    public DocumentNotFoundInFilesystemException(Throwable cause) {
        super(cause);
    }
}