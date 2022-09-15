package tesseract.OTserver.objects;

public class GetDocumentResponse {

    private Long id;

    private String model;

    private String filename;

    private String password_hash;

    private String filetype;

    public GetDocumentResponse(Long id, String model, String filename, String password_hash, String filetype) {
        this.id = id;
        this.model = model;
        this.filename = filename;
        this.password_hash = password_hash;
        this.filetype = filetype;
    }

    public GetDocumentResponse() {}

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }
}
