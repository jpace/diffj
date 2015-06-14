package org.incava.diffj.code.statements;

import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.util.DiffPoint;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDiffer extends Differ<Statement, StatementListDifference> {
    private final StatementList fromStatements;
    private final StatementList toStatements;
    
    public StatementListDiffer(Block fromBlock, Block toBlock) {
        super(fromBlock.getStatements(), toBlock.getStatements());
        
        this.fromStatements = new StatementList(fromBlock);
        this.toStatements = new StatementList(toBlock);
    }

    public StatementListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        DiffPoint delPoint = new DiffPoint(delStart, delEnd);
        DiffPoint addPoint = new DiffPoint(addStart, addEnd);

        if (delEnd == Difference.NONE) {
            return new StatementListDifferenceAdd(fromStatements, toStatements, delPoint, addPoint);
        }
        else if (addEnd == Difference.NONE) {
            return new StatementListDifferenceDelete(fromStatements, toStatements, delPoint, addPoint);
        }
        else {
            return new StatementListDifferenceChange(fromStatements, toStatements, delPoint, addPoint);
        }
    }
}
