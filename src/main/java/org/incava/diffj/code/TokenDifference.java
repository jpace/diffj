package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public abstract class TokenDifference extends Difference {
    public TokenDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public abstract FileDiff execute(Code code, LocationRange fromLocRg, LocationRange toLocRg, Differences differences);
}
