package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public class TokenDifference extends Difference {
    public TokenDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
    }

    public FileDiff execute(Code code, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        if (isAdd()) {
            return code.codeAdded(fromLocRg, toLocRg, differences);
        }
        else if (isDelete()) {
            return code.codeRemoved(fromLocRg, toLocRg, differences);
        }
        else {
            return code.codeChanged(fromLocRg, toLocRg, differences);
        }
    }
}
