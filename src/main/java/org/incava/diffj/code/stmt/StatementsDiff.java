package org.incava.diffj.code.stmt;

import org.incava.diff.Difference;
import org.incava.diffj.element.Differences;
import org.incava.diffj.util.DiffPoint;

public abstract class StatementsDiff extends Difference {
    protected final StatementList fromStatements;
    protected final StatementList toStatements;
    private final DiffPoint delPoint;
    private final DiffPoint addPoint;

    public StatementsDiff(StatementList fromStatements, StatementList toStatements,
                          DiffPoint delPoint, DiffPoint addPoint) {
        super(delPoint.getStart(), delPoint.getEnd(), addPoint.getStart(), addPoint.getEnd());
        this.fromStatements = fromStatements;
        this.toStatements = toStatements;
        this.delPoint = delPoint;
        this.addPoint = addPoint;
    }

    public void execute(String name, Differences differences) {
        process(name, fromStatements, toStatements, differences);
    }

    public abstract void process(String name, StatementList fromStatements, StatementList toStatements, Differences differences);

    public DiffPoint getDeletedPoint() {
        return delPoint;
    }

    public DiffPoint getAddedPoint() {
        return addPoint;
    }
}
