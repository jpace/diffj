package org.incava.diffj.code.statements;

import org.incava.diffj.element.Differences;
import org.incava.ijdk.util.diff.Difference;

public abstract class StatementListDifference extends Difference {
    protected final StatementList fromStatements;
    protected final StatementList toStatements;

    public StatementListDifference(StatementList fromStatements, StatementList toStatements,
                                   Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
        tr.Ace.log("delStart", delStart);
        tr.Ace.log("delEnd", delEnd);

        tr.Ace.log("addStart", addStart);
        tr.Ace.log("addEnd", addEnd);
        
        this.fromStatements = fromStatements;
        this.toStatements = toStatements;
    }

    public void execute(String name, Differences differences) {
        process(name, fromStatements, toStatements, differences);
    }

    public abstract void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences);
}
