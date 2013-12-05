package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenDifferenceAdd extends TokenDifference {
    public TokenDifferenceAdd(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public FileDiff execute(String name, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = Code.CODE_ADDED.format(name);        
        // this will show as add when highlighted, as change when not.
        FileDiff fileDiff = new FileDiffCodeAdded(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }
}
