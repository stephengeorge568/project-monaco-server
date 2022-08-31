package tesseract.OTserver.objects;

/**
 * Class that defines the range of a string change request. Mirrors Monaco Code Editor's range data model in TS
 */
public class MonacoRange {

    private Integer endColumn;
    private Integer startColumn;
    private Integer endLineNumber;
    private Integer startLineNumber;

    public MonacoRange(Integer startColumn, Integer endColumn, Integer startLineNumber, Integer endLineNumber) {
        this.endColumn = endColumn;
        this.startColumn = startColumn;
        this.endLineNumber = endLineNumber;
        this.startLineNumber = startLineNumber;
    }
    public MonacoRange() {

    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(Integer endColumn) {
        this.endColumn = endColumn;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    public Integer getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(Integer endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public Integer getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(Integer startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public boolean isEqual(MonacoRange range) {
        return this.getStartColumn() == range.getStartColumn() &&
                this.getEndColumn() == range.getEndColumn() &&
                this.getStartLineNumber() == range.getStartLineNumber() &&
                this.getEndLineNumber() == range.getEndLineNumber();
    }

    @Override
    public String toString() {
        return String.format("SC: %d\nEC: %d\nSL: %d\nEL: %d\n", startColumn, endColumn, startLineNumber, endLineNumber);
    }

}
