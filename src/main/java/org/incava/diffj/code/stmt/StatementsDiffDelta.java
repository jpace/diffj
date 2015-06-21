package org.incava.diffj.code.stmt;

import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.diffj.util.DiffPoint;
import org.incava.ijdk.text.LocationRange;

public abstract class StatementsDiffDelta extends StatementsDiff {
    public StatementsDiffDelta(StatementList fromStatements, StatementList toStatements,
                               DiffPoint delPoint, DiffPoint addPoint) {
        super(fromStatements, toStatements, delPoint, addPoint);
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
