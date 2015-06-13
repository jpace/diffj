package org.incava.diffj.code.statements;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.diffj.code.Code;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceAdd extends StatementListDifferenceDelta {
    public StatementListDifferenceAdd(StatementList fromStatements, StatementList toStatements,
                                      Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_ADDED.format(name);
    }

    public FileDiff getFileDiff(String msg, LocationRange fromLocRg, LocationRange toLocRg) {
        return new FileDiffCodeAdded(msg, fromLocRg, toLocRg);
    }

    public LocationRange getFromRange(StatementList fromStatements) {
        return fromStatements.getRangeAt(getDeletedStart());
    }

    public LocationRange getToRange(StatementList toStatements) {
        return toStatements.getRangeOf(getAddedStart(), getAddedEnd());
    }
}
