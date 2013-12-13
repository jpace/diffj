package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class TokenDifferenceDelete extends TokenDifference {
    public TokenDifferenceDelete(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public Message getMessage() {
        return Code.CODE_REMOVED;
    }

    public FileDiff getFileDiff(String str, LocationRange fromLocRg, LocationRange toLocRg) {
        return new FileDiffCodeDeleted(str, fromLocRg, toLocRg);
    }
}
