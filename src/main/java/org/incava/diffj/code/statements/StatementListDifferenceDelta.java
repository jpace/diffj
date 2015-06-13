package org.incava.diffj.code.statements;

import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;

public abstract class StatementListDifferenceDelta extends StatementListDifference {
    public StatementListDifferenceDelta(StatementList fromStatements, StatementList toStatements,
                                        Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public abstract String getMessage(String name);

    public abstract FileDiff getFileDiff(String msg, LocationRange fromLocRg, LocationRange toLocRg);

    public abstract LocationRange getFromRange(StatementList fromStatements);

    public abstract LocationRange getToRange(StatementList toStatements);

    public void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences) {
        LocationRange fromLocRg = getFromRange(fromStatements);
        LocationRange toLocRg = getToRange(toStatements);

        String msg = getMessage(name);
        FileDiff fileDiff = getFileDiff(msg, fromLocRg, toLocRg);
        differences.add(fileDiff);
    }
}
