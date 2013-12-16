package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceDelete extends StatementListDifferenceDelta {
    public StatementListDifferenceDelete(StatementList fromStatements, StatementList toStatements,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_REMOVED.format(name);
    }

    public FileDiff getFileDiff(String msg, LocationRange fromLocRg, LocationRange toLocRg) {
        return new FileDiffCodeDeleted(msg, fromLocRg, toLocRg);
    }

    public LocationRange getFromRange(StatementList fromStatements) {
        return fromStatements.getRangeOf(getDeletedStart(), getDeletedEnd());
    }

    public LocationRange getToRange(StatementList toStatements) {
        return toStatements.getRangeAt(getAddedStart());
    }
}
