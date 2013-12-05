package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenDifferenceChange extends TokenDifference {
    public TokenDifferenceChange(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public FileDiff execute(String name, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = Code.CODE_CHANGED.format(name);
        FileDiff fileDiff = new FileDiffChange(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }
}
