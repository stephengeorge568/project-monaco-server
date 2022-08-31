package tesseract.OTserver.util;

import org.springframework.web.client.HttpServerErrorException;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.Pair;
import tesseract.OTserver.objects.StringChangeRequest;

import java.util.*;

public class OperationalTransformation {

    /**
     * Transforms the given request based on the history of committed requests
     * @param request the request to transform
     * @param history the history of committed requests
     * @return
     */
    public static ArrayList<StringChangeRequest> transform(StringChangeRequest request,
                                                         HashMap<Integer, ArrayList<StringChangeRequest>> history) {

        ArrayList<StringChangeRequest> transformedRequests = new ArrayList<>();

        // Queue of requests that still need to be transformed.
        // Caused by resolveConflictingRanges sometimes creating another request
        // as a byproduct, which also must be transformed.
        Queue<Pair<StringChangeRequest, Integer>> toTransformQueue = new LinkedList<>();
        toTransformQueue.add(new Pair(request, -1));

        Pair<StringChangeRequest, Integer> currentRequest;
        while ((currentRequest = toTransformQueue.poll()) != null) {

            ArrayList<StringChangeRequest> relevantHistory = getRelevantHistory(request.getRevID(), history);

            for (int i = 0; i < relevantHistory.size(); i++) {
                StringChangeRequest historicalRequest = relevantHistory.get(i);
                // If previous request and current request share same identity, ignore, transform otherwise
                if (!request.getIdentity().equals(historicalRequest.getIdentity()) || historicalRequest.getIdentity() == -1) {
                    StringChangeRequest pair[] = MonacoRangeUtil.resolveConflictingRanges(historicalRequest, currentRequest.getKey());

                    // current request may have been added due to resolveConflictingRanges
                    // so this makes sure that historicalRequests that have already altered currentRequest
                    // do not transform currentRequest again
                    if (currentRequest.getValue() <= i) {
                        transformOperation(historicalRequest, pair[0]);
                    }

                    // If resolve conflicting ranges produced a second change, enqueue that request
                    if (pair[1] != null) {
                        toTransformQueue.add(new Pair<StringChangeRequest, Integer>(pair[1], i));
                    }
                }
            }

            // Transform current request based on previous current requests.
            for (StringChangeRequest newHistoricalRequest : transformedRequests) {
                if (MonacoRangeUtil.isPreviousRequestRelevent(newHistoricalRequest.getRange(), currentRequest.getKey().getRange()))
                    transformOperation(newHistoricalRequest, currentRequest.getKey());
            }

            transformedRequests.add(currentRequest.getKey());
        }

        return transformedRequests;
    }

    /**
     * Get the historical requests that affect the current request based on its revID
     * oldest changes at head of list, ascending order
     * @param revID the revID of the current request
     * @param history the history map
     * @return list of requests that affect the current request
     */
    private static ArrayList<StringChangeRequest> getRelevantHistory(Integer revID, HashMap<Integer, ArrayList<StringChangeRequest>> history) {
        ArrayList<StringChangeRequest> relevantRequests = new ArrayList<>();

        history.forEach(((id, list) -> {

            if (id >= revID) {
                relevantRequests.addAll(list);
            }
        }));

        return relevantRequests;
    }

    /**
     * Returns the transformed version of next based on the prev historical request.
     * @param prev the previous request, which serves as the basis on which to transform next
     * @param next the current request to transform
     * @return the transformed version of next that was altered based on prev's range and text
     */
    public static StringChangeRequest transformOperation(StringChangeRequest prev, StringChangeRequest next) {

        //region Code grouping for shorthand names for commonly used getters
        int prevSC = prev.getRange().getStartColumn();
        int prevEC = prev.getRange().getEndColumn();
        int prevSL = prev.getRange().getStartLineNumber();
        int prevEL = prev.getRange().getEndLineNumber();
        int nextSC = next.getRange().getStartColumn();
        int nextEC = next.getRange().getEndColumn();
        int nextSL = next.getRange().getStartLineNumber();
        int nextEL = next.getRange().getEndLineNumber();
        //endregion

        int newSC = nextSC;
        int newEC = nextEC;
        int newSL = nextSL;
        int newEL = nextEL;
        int numberOfNewLinesInPrev = (int) prev.getText().chars().filter(x -> x == '\n').count();

        int prevTextLengthAfterLastNewLine = prev.getText().length();

        if (numberOfNewLinesInPrev > 0) {
            prevTextLengthAfterLastNewLine = prev.getText().length() - prev.getText().lastIndexOf("\n") - 1;
        }

        if (MonacoRangeUtil.isPreviousRequestRelevent(prev.getRange(), next.getRange())) {

            int netPrevNewLineNumberChange = numberOfNewLinesInPrev
                    - (prevEL - prevSL);

            boolean isPrevSimpleInsert = prevSC == prevEC
                    && prevSL == prevEL;

            if (isPrevSimpleInsert) {
                if (numberOfNewLinesInPrev > 0) {
                    if (nextSL == prevEL) {
                        newSC = newSC - prevEC + prevTextLengthAfterLastNewLine + 1;
                    } if (nextEL == prevEL) {
                        newEC = newEC - prevEC + prevTextLengthAfterLastNewLine + 1;
                    }
                } else {
                    if (nextSL == prevEL) {
                        newSC = newSC + prevTextLengthAfterLastNewLine;
                    } if (nextEL == prevEL) {
                        newEC = newEC + prevTextLengthAfterLastNewLine;
                    }
                }
            } else {
                if (numberOfNewLinesInPrev > 0) {
                    if (nextSL == prevEL) {
                        newSC = (newSC - prevEC) + prevTextLengthAfterLastNewLine + 1;
                    }
                    if (nextEL == prevEL) {
                        newEC = (newEC - prevEC) + prevTextLengthAfterLastNewLine + 1;
                    }
                } else {
                    int numberOfCharsDeletedOnPrevLine = prevEC - prevSC;

                    if (nextSL == prevEL) {
                        newSC = newSC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    } else { // next start is on diff line than prev start but still within range
                        newSC = prevSC + prev.getText().length();
                    }
                    
                    if (nextEL == prevEL) {
                        newEC = newEC - numberOfCharsDeletedOnPrevLine + prev.getText().length();
                    } else {
                        // If next is simple insert, EC must be also set same as SC.
                        // If next is not simple insert, EC does not have to change because only
                        // the line number will shift. Text does not wrap in code editor format.
                        if (MonacoRangeUtil.isRangeSimpleInsert(next.getRange())) {
                            newEC = newSC;
                        }
                    }
                    // deal with new on diff line (but still inside prev range)
                }
            }

            // If next start column within range of prev, push next.SL to prev.SL + # of new lines in prev
            if (MonacoRangeUtil.isSCWithinRange(prev.getRange(), next.getRange())) {
                newSL = prevSL + numberOfNewLinesInPrev;
            } else {
                newSL += netPrevNewLineNumberChange;
            }

            if (MonacoRangeUtil.isECWithinRange(prev.getRange(), next.getRange())) {
                newEL = prevSL + numberOfNewLinesInPrev;
            } else {
                newEL += netPrevNewLineNumberChange;
            }
        }

        next.setRange(new MonacoRange(newSC, newEC, newSL, newEL));
        return next;
    }

}
