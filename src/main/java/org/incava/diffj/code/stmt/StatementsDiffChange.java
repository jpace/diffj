package org.incava.diffj.code.stmt;

import org.incava.diffj.code.Code;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.element.Differences;
import org.incava.diffj.util.DiffPoint;

public class StatementsDiffChange extends StatementsDiff {
    public StatementsDiffChange(StatementList fromStatements, StatementList toStatements,
                                         DiffPoint delPoint, DiffPoint addPoint) {
        super(fromStatements, toStatements, delPoint, addPoint);
    }

    public void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences) {
        TokenList fromList = fromStatements.getAsTokenList(getDeletedPoint());
        TokenList toList = toStatements.getAsTokenList(getAddedPoint());

        Code fc = new Code(name, fromList);
        Code tc = new Code(name, toList);
        fc.diff(tc, differences);
    }
}
