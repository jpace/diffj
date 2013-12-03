package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenDifferenceAdd extends TokenDifference {
    public TokenDifferenceAdd(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public FileDiff execute(Code code, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        return code.codeAdded(fromLocRg, toLocRg, differences);
    }
}
