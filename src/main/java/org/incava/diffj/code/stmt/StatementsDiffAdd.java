package org.incava.diffj.code.stmt;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.diffj.code.Code;
import org.incava.diffj.util.DiffPoint;
import org.incava.ijdk.text.LocationRange;

public class StatementsDiffAdd extends StatementsDiffDelta {
    public StatementsDiffAdd(StatementList fromStatements, StatementList toStatements,
                             DiffPoint delPoint, DiffPoint addPoint) {
        super(fromStatements, toStatements, delPoint, addPoint);
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
        return toStatements.getRangeOf(getAddedPoint());
    }
}
