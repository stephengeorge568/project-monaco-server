package tesseract.OTserver.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest
public class OperationalTransformationTests {
    // https://www.baeldung.com/junit-5

    @Test
    void contextLoads() {
    }

    @Test
    void transformOperation_LinesChangeWhenPrevAboveNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(4,8,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,6, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(3,6,4,4));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_TwoSimpleInsertsPrevBefore() {
        StringChangeRequest prev = new StringChangeRequest("a", new MonacoRange(3,3,1,1 ));
        StringChangeRequest next = new StringChangeRequest("b", new MonacoRange(6,6, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("b", new MonacoRange(6,6, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("b", new MonacoRange(7,7,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_LinesChangeWhenPrevSameNext() {
        StringChangeRequest prev = new StringChangeRequest("a\nb\nc", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(4,4,3,3));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_ShiftRightNoNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));

        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(6,6,1,1));

        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_ShiftRightNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefgh", new MonacoRange(1,1,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(3,3, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(8,8,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text=""
        turns into
    N    |-----|
    P
     */
    @Test
    void transformOperation_SelectionDelete() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(7,9, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(4,9, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(4,6,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }


    @Test
    void transformOperation_ShiftRightNewLines2() {
        StringChangeRequest prev = new StringChangeRequest("\n", new MonacoRange(2,2,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6,6, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6,6, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5,5,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text="abc"
        turns into
    N           |-----|
    P |-----|               abc same length as being deleted. stays same
     */
    @Test
    void transformOperation_SelectionReplaceSameNoNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abc", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(9,12,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    /*
    N           |-----|
    P |-----|               p.text="abcdef"
        turns into
    N              |-----|
    P |-----|
     */
    @Test
    void transformOperation_SelectionReplaceNoNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abcdef", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(12,15,1,1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionReplaceNewLines() {
        StringChangeRequest prev = new StringChangeRequest("abc\n", new MonacoRange(2,5,1,1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(9,12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5,8,2,2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev,next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionReplaceNewLinesTextAfterNewLine() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefg", new MonacoRange(1, 4, 1, 1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(7, 13, 2, 2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_SelectionDiffLinesNoNewLineRemoved() {
        StringChangeRequest prev = new StringChangeRequest("abc\ndefg", new MonacoRange(1, 4, 1, 1 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(6, 12, 3, 3));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_DeletionDiffLinesNewLineRemoved() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(3, 4, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(5, 11, 1, 1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_DeletionDiffLinesNewLineRemovedTextAdded() {
        StringChangeRequest prev = new StringChangeRequest("qtf", new MonacoRange(3, 4, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest nextCopy = new StringChangeRequest("ohno", new MonacoRange(6, 12, 2, 2));
        StringChangeRequest expe = new StringChangeRequest("ohno", new MonacoRange(8, 14, 1, 1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_prevRangeDeletedRightBeforeNextRange() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(1, 7, 1, 1 ));
        StringChangeRequest next = new StringChangeRequest("g", new MonacoRange(7, 7, 1, 1));
        StringChangeRequest nextCopy = new StringChangeRequest("g", new MonacoRange(7, 7, 1, 1));
        StringChangeRequest expe = new StringChangeRequest("g", new MonacoRange(1, 1, 1, 1));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_prevIsRangeNoNewLinesInPrevPrevELAfterNextEL() {
        StringChangeRequest prev = new StringChangeRequest("abc", new MonacoRange(3, 6, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("q", new MonacoRange(6, 3, 2, 3));
        StringChangeRequest nextCopy = new StringChangeRequest("q", new MonacoRange(6, 3, 2, 3));
        StringChangeRequest expe = new StringChangeRequest("q", new MonacoRange(6, 3, 1, 2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transformOperation_prevIsRangeNoNewLinesInPrevPrevELAfterNextEL_LongerPrevText() {
        StringChangeRequest prev = new StringChangeRequest("abcdef", new MonacoRange(3, 6, 1, 2 ));
        StringChangeRequest next = new StringChangeRequest("q", new MonacoRange(6, 3, 2, 3));
        StringChangeRequest nextCopy = new StringChangeRequest("q", new MonacoRange(6, 3, 2, 3));
        StringChangeRequest expe = new StringChangeRequest("q", new MonacoRange(9, 3, 1, 2));
        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);

        printTransOpTest(prev, nextCopy, tran, expe);
        assertEquals(true, expe.isEqual(tran));
    }

    @Test
    void transform_OnlyFirstHistoryRelevant() {
        StringChangeRequest request = new StringChangeRequest("a", new MonacoRange(5, 5, 1, 1 ), 1);
        request.setIdentity(1);
        StringChangeRequest history1 = new StringChangeRequest("", new MonacoRange(1, 5, 1, 1), 1);
        history1.setIdentity(2);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("a", new MonacoRange(1, 1, 1, 1), 1);
        //StringChangeRequest expe2 = new StringChangeRequest(null);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed:\n" + trans.get(0).toString());
        System.out.println("Expected:\n" + expe1.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        //assertEquals(true, trans.get(1) == null);
    }

    @Test
    void transform_test() {
        StringChangeRequest request = new StringChangeRequest("d", new MonacoRange(39, 39, 1, 1 ), 1);
        request.setIdentity(1);
        StringChangeRequest history1 = new StringChangeRequest("a", new MonacoRange(37, 37, 1, 1), 1);
        history1.setIdentity(1);
        StringChangeRequest history2 = new StringChangeRequest("", new MonacoRange(37, 38, 1, 1), 1);
        history2.setIdentity(2);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history2);
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("d", new MonacoRange(38, 38, 1, 1), 1);
        //StringChangeRequest expe2 = new StringChangeRequest(null);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed:\n" + trans.get(0).toString());
        System.out.println("Expected:\n" + expe1.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        //assertEquals(true, trans.get(1) == null);
    }

    @Test
    void transform_test2() {
        StringChangeRequest request = new StringChangeRequest("g", new MonacoRange(35, 35, 2, 2 ), 1);
        request.setIdentity(1);
        StringChangeRequest history1 = new StringChangeRequest("u", new MonacoRange(34, 34, 2, 2), 1);
        history1.setIdentity(1);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("g", new MonacoRange(35, 35, 2, 2), 1);
        //StringChangeRequest expe2 = new StringChangeRequest(null);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed:\n" + trans.get(0).toString());
        System.out.println("Expected:\n" + expe1.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        //assertEquals(true, trans.get(1) == null);
    }

    @Test
    void transform_test3() { // restart here
        StringChangeRequest request = new StringChangeRequest("c", new MonacoRange(49, 49, 6, 6 ), 1);
        request.setIdentity(2);
        StringChangeRequest history1 = new StringChangeRequest("d", new MonacoRange(1, 51, 1, 8), 1);
        history1.setIdentity(1);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("c", new MonacoRange(2, 2, 1, 1), 1);
        //StringChangeRequest expe2 = new StringChangeRequest(null);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed:\n" + trans.get(0).toString());
        System.out.println("Expected:\n" + expe1.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        //assertEquals(true, trans.get(1) == null);
    }

    @Test
    void transform_twoReturnedRequests() { // restart here
        StringChangeRequest request = new StringChangeRequest("", new MonacoRange(1, 10, 1, 1 ), 1);
        request.setIdentity(2);
        StringChangeRequest history1 = new StringChangeRequest("", new MonacoRange(3, 7, 1, 1), 1);
        history1.setIdentity(1);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("", new MonacoRange(1, 3, 1, 1), 1);
        StringChangeRequest expe2 = new StringChangeRequest("", new MonacoRange(1, 4, 1, 1), 1);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed: " + trans.get(0).toString());
        System.out.println("Expected:    " + expe1.toString());
        System.out.println("Transformed: " + trans.get(1).toString());
        System.out.println("Expected:    " + expe2.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        assertEquals(true, trans.get(1).isEqual(expe2));
        //assertEquals(true, trans.get(1) == null);
    }

    @Test
    void transform_twoReturnedRequests_test() { // restart here
        StringChangeRequest request = new StringChangeRequest("", new MonacoRange(1, 40, 1, 1 ), 1);
        request.setIdentity(2);
        StringChangeRequest history1 = new StringChangeRequest("", new MonacoRange(7, 27, 1, 1), 1);
        history1.setIdentity(1);
        ArrayList<StringChangeRequest> historyList = new ArrayList<>();
        historyList.add(history1);

        HashMap<Integer, ArrayList<StringChangeRequest>> history = new HashMap<>();
        history.put(1, historyList);
        StringChangeRequest expe1 = new StringChangeRequest("", new MonacoRange(1, 7, 1, 1), 1);
        StringChangeRequest expe2 = new StringChangeRequest("", new MonacoRange(1, 14, 1, 1), 1);
        ArrayList<StringChangeRequest> trans = OperationalTransformation.transform(request, history);

        System.out.println("Transformed: " + trans.get(0).toString());
        System.out.println("Expected:    " + expe1.toString());
        System.out.println("Transformed: " + trans.get(1).toString());
        System.out.println("Expected:    " + expe2.toString());

        assertEquals(true, trans.get(0).isEqual(expe1));
        assertEquals(true, trans.get(1).isEqual(expe2));
        //assertEquals(true, trans.get(1) == null);
    }


/*
StringChangeRequest{timestamp='2022-07-25T04:59:57.790Z', text='', identity='1', rangeSC=7, rangeEC=27, rangeSL=1, rangeEL=1, revID=40, setID=null}
StringChangeRequest{timestamp='2022-07-25T04:59:57.869Z', text='', identity='2', rangeSC=1, rangeEC=40, rangeSL=1, rangeEL=1, revID=40, setID=null}
 */

//    @Test
//    void transformOperation_PrevInsideNextReplaceWithText() {
//        StringChangeRequest prev = new StringChangeRequest("123", new MonacoRange(6, 2, 1, 2 ));
//        StringChangeRequest next = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));
//        StringChangeRequest nextCopy = new StringChangeRequest("abcdef", new MonacoRange(4, 4, 1, 2));
//        StringChangeRequest expe = new StringChangeRequest("abcdef", new MonacoRange(4, 11, 1, 1));
//        StringChangeRequest tran = OperationalTransformation.transformOperation(prev, next);
//
//        printTransOpTest(prev, nextCopy, tran, expe);
//        assertEquals(true, expe.isEqual(tran));
//    }

    private void printTransOpTest(StringChangeRequest prev, StringChangeRequest next,
                                  StringChangeRequest transformed,StringChangeRequest expe) {

        // I should make this cleaner ... but I won't
        String output = String.format("  \tPV\tNX\tTF\tEX\t\n" +
                      "SC\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "EC\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "SL\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "EL\t%2d\t%2d\t%2d\t%2d\t\n" +
                      "%s\t%s\t%s\t%s", prev.getRange().getStartColumn(), next.getRange().getStartColumn(), transformed.getRange().getStartColumn(), expe.getRange().getStartColumn(),
                prev.getRange().getEndColumn(), next.getRange().getEndColumn(), transformed.getRange().getEndColumn(), expe.getRange().getEndColumn(),
                prev.getRange().getStartLineNumber(), next.getRange().getStartLineNumber(), transformed.getRange().getStartLineNumber(), expe.getRange().getStartLineNumber(),
                prev.getRange().getEndLineNumber(), next.getRange().getEndLineNumber(), transformed.getRange().getEndLineNumber(), expe.getRange().getEndLineNumber(),
                prev.getText(), next.getText(), transformed.getText(), expe.getText());
        System.out.println(output);
    }


}

