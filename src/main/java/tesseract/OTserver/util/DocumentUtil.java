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
        int newLineCount = 0;
        int colCount = 0;

        // TODO just use model string
        char[] arr = model.toCharArray();
        while (index < arr.length && newLineCount < line - 1) {
            if (arr[index] == '\n') newLineCount++;
            index++;
        }
        while (index < arr.length && colCount < col - 1) {
            colCount++;
            index++;
        }

        return index;
    }

}
