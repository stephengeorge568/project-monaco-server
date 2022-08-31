package tesseract.OTserver.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

@SpringBootTest
public class MonacoRangeUtilTests {
    // https://www.baeldung.com/junit-5
    @Test
    void contextLoads() {
    }

    // play with naming this feels weird
    // startColumn, endColumn, startLineNumber, endLineNumber
    @Test
    void isPreviousRequestRelevent_assertTrue_PrevLineAndColumnAfterNext() {
        MonacoRange prev = new MonacoRange(1,1,1,1);
        MonacoRange next = new MonacoRange(2,2,2,2);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevLineAndColumnBeforeNext() {
        MonacoRange next = new MonacoRange(1,1,1,1);
        MonacoRange prev = new MonacoRange(2,2,2,2);
        assertEquals(false, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInFront() {
        MonacoRange prev = new MonacoRange(3,7,1,2);
        MonacoRange next = new MonacoRange(5,9,2,2);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_IntersectingRangesPrevInBack() {
        MonacoRange next = new MonacoRange(3,7,1,2);
        MonacoRange prev = new MonacoRange(5,9,2,2);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_SameRange() {
        MonacoRange prev = new MonacoRange(1,1,1,1);
        MonacoRange next = new MonacoRange(1,1,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertFalse_PrevECIgnoredBecauseItsRange() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(1,2,1,1);
        assertEquals(false, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_PrevECConsideredBecauseSimpleInsert() {
        MonacoRange prev = new MonacoRange(2,2,1,1);
        MonacoRange next = new MonacoRange(2,2,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextRangeBeforePrevRangeOverlapSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(4,9,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    @Test
    void isPreviousRequestRelevent_assertTrue_NextPrevSameSelection() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(2,6,1,1);
        assertEquals(true, MonacoRangeUtil.isPreviousRequestRelevent(prev, next));
    }

    /* ------------------------------------------------------------------------------------------------------------- */

    /*
    N |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_Same() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(2,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }


    /*
    N   |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_PrevBeforeNext1Line() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(1,4,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_NextBeforePrev1Line() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(5,9,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P        |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_NextBeforePrev1LineNoGap() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(6,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N  |-----|
    P         |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_NextBeforePrev1LineGap() {
        MonacoRange next = new MonacoRange(2,6,1,1);
        MonacoRange prev = new MonacoRange(7,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N       |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_PrevBeforeNext1LineNoGap() {
        MonacoRange prev = new MonacoRange(2,6,1,1);
        MonacoRange next = new MonacoRange(6,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N        |-----|
    P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_PrevBeforeNext1LineGap() {
        MonacoRange prev = new MonacoRange(2,6,1,1);
        MonacoRange next = new MonacoRange(7,9,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_PrevInsideNext1Line() {
        MonacoRange prev = new MonacoRange(2,9,1,1);
        MonacoRange next = new MonacoRange(4,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   |-----|
     */
    @Test
    void isRangeOverlap_assertTrue_NextInsidePrev1Line() {
        MonacoRange next = new MonacoRange(2,9,1,1);
        MonacoRange prev = new MonacoRange(4,6,1,1);
        assertEquals(true, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P   \n|-----|
     */
    @Test
    void isRangeOverlap_assertFalse_Both1LineButPrevBelowNext() {
        MonacoRange prev = new MonacoRange(2,9,2,2);
        MonacoRange next = new MonacoRange(4,6,1,1);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |-----\n
      ---| P |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_Next2LineStopsAtNextSC() {
        MonacoRange prev = new MonacoRange(4,9,2,2);
        MonacoRange next = new MonacoRange(1,4,1,2);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /*
    N |---------|
    P  \n\n\n |-----|
     */
    @Test
    void isRangeOverlap_assertFalse_WayDifferntLines() {
        MonacoRange prev = new MonacoRange(4,9,6,9);
        MonacoRange next = new MonacoRange(1,4,1,2);
        assertEquals(false, MonacoRangeUtil.isRangeOverlap(prev, next));
    }

    /* ------------------------------------------------------------------------------------------------------------- */

    /*
    N |---------|
    P        |-----|
        turns into
    N |------|
    P        |-----|
     */
    @Test
    void resolveConflictingRanges_NextEndWithinPrevRange() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(1,5,1,1));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(3,8,1,1));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);
        System.out.println(requests[0].getRange().toString());
        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(1,3,1,1)));
    }

    /*
    N     |-----|
    P |-----|
        turns into
    N       |---|
    P |-----|
     */
    @Test
    void resolveConflictingRanges_NextStartWithinPrevRange() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(6,12,1,1));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(3,8,1,1));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);

        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(8,12,1,1)));
    }

    /*
    N |-----|
    P |-----|
        turns into
    N       |
    P |-----|
     */
    @Test
    void resolveConflictingRanges_SameRange() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(3,8,1,1));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(3,8,1,1));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(next, prev);

        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(8,8,1,1)));
    }

    /*
    N   |-----|
    P |---------|
        turns into
    N           |
    P |---------|
     */
    @Test
    void resolveConflictingRanges_NextContainedInsidePrev() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(3,6,1,1));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(1,8,1,1));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);

        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(8,8,1,1)));
    }

    /*
    N |---------|
    P   |-----|
        turns into
    N |-|     |-|
    P   |-----|
     */
    @Test
    void resolveConflictingRanges_PrevContainedInsideNext2Requests() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(1,8,1,1));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(3,6,1,1));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);

        assertEquals(true, requests[1] != null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(1,3,1,1)));
        assertEquals(true, requests[1].getRange().isEqual(new MonacoRange(6,8,1,1)));
    }

    /*
    N |---------|
    P        |-----|
        turns into
    N |------|
    P        |-----|
     */
    @Test
    void resolveConflictingRanges_NextEndWithinPrevRange_Multiline() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(1,5,1,2));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(2,6,2,3));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);
        //System.out.println(requests[0].getRange().toString());
        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(1,2,1,2)));
    }

    /*
    N     |-----|
    P |-----|
        turns into
    N       |---|
    P |-----|
     */
    @Test
    void resolveConflictingRanges_NextStartWithinPrevRange_Multiline() {
        StringChangeRequest next = new StringChangeRequest("abc", new MonacoRange(6,12,2,2));
        StringChangeRequest prev = new StringChangeRequest("def", new MonacoRange(3,8,1,2));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);

        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(8,12,2,2)));
    }

    @Test
    void resolveConflictingRanges_PrevEndsPlus1AfterNextStart_MultiLine() {
        StringChangeRequest prev = new StringChangeRequest("", new MonacoRange(3,6,1,2));
        StringChangeRequest next = new StringChangeRequest("", new MonacoRange(5,4,2,3));

        StringChangeRequest[] requests = MonacoRangeUtil.resolveConflictingRanges(prev, next);

        assertEquals(true, requests[1] == null);
        assertEquals(true, requests[0].getRange().isEqual(new MonacoRange(6,4,2,3)));
    }

}

