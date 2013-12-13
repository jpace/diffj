package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class TokenDifferenceAdd extends TokenDifference {
    public TokenDifferenceAdd(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public Message getMessage() {
        return Code.CODE_ADDED;
    }

    public FileDiff getFileDiff(String str, LocationRange fromLocRg, LocationRange toLocRg) {
        // this will show as add when highlighted, as change when not.
        return new FileDiffCodeAdded(str, fromLocRg, toLocRg);
    }
}
