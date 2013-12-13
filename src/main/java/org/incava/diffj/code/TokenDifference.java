package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;
import org.incava.ijdk.util.diff.Difference;

public abstract class TokenDifference extends Difference {
    public TokenDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public abstract FileDiff getFileDiff(String str, LocationRange fromLocRg, LocationRange toLocRg);

    public abstract Message getMessage();
    
    public FileDiff execute(String name, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        Message msg = getMessage();
        String str = msg.format(name);        
        FileDiff fileDiff = getFileDiff(str, fromLocRg, toLocRg);
        differences.add(fileDiff);
        return fileDiff;
    }

    protected FileDiff addFileDiff(FileDiff fileDiff, Differences differences) {
        differences.add(fileDiff);
        return fileDiff;
    }

    public LocationRange getDeletedRange(TokenList tokenList) {
        return tokenList.getLocationRange(getDeletedStart(), getDeletedEnd());
    }

    public LocationRange getAddedRange(TokenList tokenList) {
        return tokenList.getLocationRange(getAddedStart(), getAddedEnd());
    }
}
