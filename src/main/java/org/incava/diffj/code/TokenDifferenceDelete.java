package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenDifferenceDelete extends TokenDifference {
    public TokenDifferenceDelete(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public FileDiff execute(String name, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = Code.CODE_REMOVED.format(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }
}
