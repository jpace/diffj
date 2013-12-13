package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public abstract class TokenDifference extends Difference {
    public TokenDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public abstract FileDiff execute(String name, LocationRange fromLocRg, LocationRange toLocRg, Differences differences);

    protected FileDiff addFileDiff(FileDiff fileDiff, Differences differences) {
        differences.add(fileDiff);
        return fileDiff;
    }

    public LocationRange getDeletedRange(TokenList tokenList) {
        tr.Ace.onRed("getDeletedEnd()", getDeletedEnd());
        LocationRange lr = tokenList.getLocationRange(getDeletedStart(), getDeletedEnd());
        tr.Ace.log("lr", lr);
        return lr;
    }

    public LocationRange getAddedRange(TokenList tokenList) {
        return tokenList.getLocationRange(getAddedStart(), getAddedEnd());
    }
}
