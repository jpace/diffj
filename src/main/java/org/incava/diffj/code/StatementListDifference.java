package org.incava.diffj.code;

import java.util.List;
import org.incava.diffj.element.Differences;

public abstract class StatementListDifference extends Difference {
    private final StatementList fromStatements;
    private final StatementList toStatements;

    public StatementListDifference(List<Statement> fromStatements, List<Statement> toStatements,
                                   Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
        this.fromStatements = new StatementList(fromStatements);
        this.toStatements = new StatementList(toStatements);
    }

    public void execute(String name, Differences differences) {
        process(name, getFromList(), getToList(), differences);
    }

    public abstract void process(String name, TokenList fromList, TokenList toList, Differences differences);

    public TokenList getFromList() {
        return fromStatements.getAsTokenList(getDeletedStart(), getDeletedEnd());
    }

    public TokenList getToList() {
        return toStatements.getAsTokenList(getAddedStart(), getAddedEnd());
    }
}
