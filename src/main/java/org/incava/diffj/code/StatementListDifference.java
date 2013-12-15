package org.incava.diffj.code;

import org.incava.diffj.element.Differences;
import org.incava.ijdk.util.diff.Difference;

public abstract class StatementListDifference extends Difference {
    private final StatementList fromStatements;
    private final StatementList toStatements;

    public StatementListDifference(StatementList fromStatements, StatementList toStatements,
                                   Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
        this.fromStatements = fromStatements;
        this.toStatements = toStatements;
    }

    public void execute(String name, Differences differences) {
        TokenList fromList = fromStatements.getAsTokenList(getDeletedStart(), getDeletedEnd());
        TokenList toList = toStatements.getAsTokenList(getAddedStart(), getAddedEnd());
        
        process(name, fromList, toList, differences);
    }

    public abstract void process(String name, TokenList fromList, TokenList toList, Differences differences);
}
