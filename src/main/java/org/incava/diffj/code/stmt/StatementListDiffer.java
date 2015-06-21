package org.incava.diffj.code.stmt;

import org.incava.diff.Differ;
import org.incava.diff.Difference;
import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.util.DiffPoint;

public class StatementListDiffer extends Differ<Statement, StatementsDiff> {
    private final StatementList fromStatements;
    private final StatementList toStatements;
    
    public StatementListDiffer(Block fromBlock, Block toBlock) {
        super(fromBlock.getStatements(), toBlock.getStatements());
        
        this.fromStatements = new StatementList(fromBlock);
        this.toStatements = new StatementList(toBlock);
    }

    public StatementsDiff createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        DiffPoint delPoint = new DiffPoint(delStart, delEnd);
        DiffPoint addPoint = new DiffPoint(addStart, addEnd);

        if (delEnd == Difference.NONE) {
            return new StatementsDiffAdd(fromStatements, toStatements, delPoint, addPoint);
        }
        else if (addEnd == Difference.NONE) {
            return new StatementsDiffDelete(fromStatements, toStatements, delPoint, addPoint);
        }
        else {
            return new StatementsDiffChange(fromStatements, toStatements, delPoint, addPoint);
        }
    }
}
