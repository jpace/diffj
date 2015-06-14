package org.incava.diffj.code.statements;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.code.Code;
import org.incava.diffj.util.DiffPoint;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceDelete extends StatementListDifferenceDelta {
    public StatementListDifferenceDelete(StatementList fromStatements, StatementList toStatements,
                                         DiffPoint delPoint, DiffPoint addPoint) {
        super(fromStatements, toStatements, delPoint, addPoint);
    }

    public String getMessage(String name) {
        return Code.CODE_REMOVED.format(name);
    }

    public FileDiff getFileDiff(String msg, LocationRange fromLocRg, LocationRange toLocRg) {
        return new FileDiffCodeDeleted(msg, fromLocRg, toLocRg);
    }

    public LocationRange getFromRange(StatementList fromStatements) {
        tr.Ace.log("fromStatements", fromStatements);
        return fromStatements.getRangeOf(getDeletedPoint());
    }

    public LocationRange getToRange(StatementList toStatements) {
        tr.Ace.log("toStatements", toStatements);
        return toStatements.getRangeAt(getAddedStart());
    }
}
