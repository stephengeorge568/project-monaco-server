package tesseract.OTserver.objects;

public class GetDocumentMetaResponse {

    private Long id;

    private String filename;

    private String filetype;

    public GetDocumentMetaResponse(Long id, String filename, String filetype) {
        this.id = id;
        this.filename = filename;
        this.filetype = filetype;
    }

    public GetDocumentMetaResponse() {}

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
