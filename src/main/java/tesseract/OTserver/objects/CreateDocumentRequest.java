package tesseract.OTserver.objects;

public class CreateDocumentRequest {

    private String password_hash;

    private String filename;

    private String filetype;

    public CreateDocumentRequest(String password_hash, String filename, String filetype) {
        this.password_hash = password_hash;
        this.filename = filename;
        this.filetype = filetype;
    }

    public CreateDocumentRequest() {}

    public String getPassword_hash() {
        return password_hash;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
