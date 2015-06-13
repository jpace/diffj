package org.incava.diffj.code.statements;

import org.incava.diffj.code.Code;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.element.Differences;

public class StatementListDifferenceChange extends StatementListDifference {
    public StatementListDifferenceChange(StatementList fromStatements, StatementList toStatements,
                                         Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
    }

    public void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences) {
        TokenList fromList = fromStatements.getAsTokenList(getDeletedStart(), getDeletedEnd());
        TokenList toList = toStatements.getAsTokenList(getAddedStart(), getAddedEnd());

        Code fc = new Code(name, fromList);
        Code tc = new Code(name, toList);
        fc.diff(tc, differences);
    }
}
