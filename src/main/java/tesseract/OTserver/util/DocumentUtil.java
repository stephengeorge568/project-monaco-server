package tesseract.OTserver.util;

import tesseract.OTserver.objects.StringChangeRequest;

public class DocumentUtil {

    /**
     * Update the model of the document by converting MonacoRange to standard string indexes.
     * @param model the current string model
     * @param req the request to commit to the model
     * @return the updated model
     */
    public static String updateModel(String model, StringChangeRequest req) {
        StringBuilder modelBuilder = new StringBuilder(model);
        boolean isSimpleInsert = req.getRange().getStartLineNumber() == req.getRange().getEndLineNumber()
                && req.getRange().getStartColumn() == req.getRange().getEndColumn();

        if (!isSimpleInsert)
            modelBuilder.replace(
                    getIndex(model, req.getRange().getStartColumn(), req.getRange().getStartLineNumber()),
                    getIndex(model, req.getRange().getEndColumn(), req.getRange().getEndLineNumber()),
                    req.getText());
        else modelBuilder.insert(
                getIndex(model, req.getRange().getStartColumn(), req.getRange().getStartLineNumber()),
                req.getText());
        return modelBuilder.toString();
    }

    /**
     * The standard string index of the request's starting or ending point
     * @param model the current model
     * @param col the column number in MonacoRange terms
     * @param line the line number in MonacoRange terms
     * @return the standard string index of a MonacoRange point
     */
    public static int getIndex(String model, int col, int line) {
        int index = 0;
        String[] lines = model.split("\n");

        for (int i = 0; i < line - 1; i++) {
            index += lines[i].length(); // plus 1 because \n is removed via model.split
        } index += col + line - 1;

        return index - 1; // -1 because string first index is 0, whereas MonacoRange first index is 1
    }

}
