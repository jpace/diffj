package org.incava.diffj.code.stmt;

import org.incava.diffj.code.Code;
import org.incava.diffj.element.Differences;
import org.incava.diffj.util.DiffPoint;

public class StatementsDiffChange extends StatementsDiff {
    public StatementsDiffChange(StatementList fromStatements, StatementList toStatements,
                                DiffPoint delPoint, DiffPoint addPoint) {
        super(fromStatements, toStatements, delPoint, addPoint);
    }

    public void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences) {
        Code fromCode = fromStatements.getAsCode(name, getDeletedPoint());
        Code toCode = toStatements.getAsCode(name, getAddedPoint());
        fromCode.diff(toCode, differences);
    }
}
