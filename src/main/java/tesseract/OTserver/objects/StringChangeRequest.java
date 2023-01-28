package tesseract.OTserver.objects;

import java.util.Objects;

/**
 * Class that defines the change operation in the code editor.
 */
public class StringChangeRequest {

    private Long documentId;

    /**
     * Timestamp of the moment the operation was created in the client editor
     */
    private String timestamp;

    /**
     * The text of the change
     */
    private String text;

    /**
     * The identifier of the client. Currently, this is the client's IP.
     */
    private Integer identity;

    /**
     * The range of the operation
     */
    private MonacoRange range;

    /**
     * The document revision ID of the client at the time the operation was committed.
     */
    private Integer revID;

    /**
     * The ID that the client will set its document revision id to after receiving this request
     */
    private Integer setID;

    private String password;


    // TODO cleanup, remove constructors, turn setters into builder
    public StringChangeRequest(String text, MonacoRange range) {
        this.text = text;
        this.range = range;
    }

    public StringChangeRequest(String text, MonacoRange range, Integer revID) {
        this.text = text;
        this.range = range;
        this.revID = revID;
    }

    public StringChangeRequest(String text, MonacoRange range, Integer revID, Integer identity) {
        this.text = text;
        this.range = range;
        this.revID = revID;
        this.identity = identity;
    }

    public StringChangeRequest(String text, MonacoRange range, Integer revID, Integer identity, Long docId) {
        this.text = text;
        this.range = range;
        this.revID = revID;
        this.identity = identity;
        this.documentId = docId;
    }

    public StringChangeRequest() {}

    // TODO deep copy constructor, convert to .clone() override
    public StringChangeRequest(StringChangeRequest other) {
        this.text = other.text;
        this.timestamp = other.timestamp;
        this.identity = other.identity;
        this.range = new MonacoRange(
                other.getRange().getStartColumn(),
                other.getRange().getEndColumn(),
                other.getRange().getStartLineNumber(),
                other.getRange().getEndLineNumber());
        this.revID = other.getRevID();
        this.setID = other.getSetID();
        this.documentId = other.getDocumentId();
        this.password = other.password;
    }

    public Integer getRevID() {
        return revID;
    }

    public void setRevID(Integer revID) {
        this.revID = revID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public StringChangeRequest setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getText() {
        return text;
    }

    public StringChangeRequest setText(String text) {
        this.text = text;
        return this;
    }


    public Integer getIdentity() {
        return identity;
    }

    public StringChangeRequest setIdentity(Integer identity) {
        this.identity = identity;
        return this;
    }



    public MonacoRange getRange() {
        return range;
    }

    public void setRange(MonacoRange range) {
        this.range = range;
    }

    public Integer getSetID() {
        return setID;
    }

    public void setSetID(Integer setID) {
        this.setID = setID;
    }


    public boolean isEqual(StringChangeRequest req) {
        return this.getRange().isEqual(req.getRange())
                && Objects.equals(this.text, req.getText());
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp='" + timestamp + '\'' +
                ", docId='" + documentId + '\'' +
                ", text='" + text + '\'' +
                ", identity='" + identity + '\'' +
                ", rangeSC=" + range.getStartColumn() +
                ", rangeEC=" + range.getEndColumn() +
                ", rangeSL=" + range.getStartLineNumber() +
                ", rangeEL=" + range.getEndLineNumber() +
                ", revID=" + revID +
                ", setID=" + setID +
                '}';
    }
}
