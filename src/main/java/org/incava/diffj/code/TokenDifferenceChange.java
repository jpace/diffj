package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class TokenDifferenceChange extends TokenDifference {
    public TokenDifferenceChange(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public Message getMessage() {
        return Code.CODE_CHANGED;
    }

    public FileDiff getFileDiff(String str, LocationRange fromLocRg, LocationRange toLocRg) {
        return new FileDiffChange(str, fromLocRg, toLocRg);
    }
}
