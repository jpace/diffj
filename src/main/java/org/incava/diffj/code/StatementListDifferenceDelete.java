package org.incava.diffj.code;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;

public class StatementListDifferenceDelete extends StatementListDifference {
    public StatementListDifferenceDelete(StatementList fromStatements, StatementList toStatements,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public String getMessage(String name) {
        return Code.CODE_REMOVED.format(name);
    }

    public void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences) {
        LocationRange flr = fromStatements.getRangeOf(getDeletedStart(), getDeletedEnd());
        LocationRange tlr = toStatements.getRangeAt(getAddedStart());

        String msg = getMessage(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(msg, flr, tlr);
        differences.add(fileDiff);
    }
}
