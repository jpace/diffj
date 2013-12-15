package org.incava.diffj.code;

import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDiffer extends Differ<Statement, StatementListDifference> {
    private final StatementList fromStatements;
    private final StatementList toStatements;
    
    public StatementListDiffer(Block fromBlock, Block toBlock) {
        super(fromBlock.getStatements(), toBlock.getStatements());
        
        this.fromStatements = new StatementList(fromBlock.getStatements());
        this.toStatements = new StatementList(toBlock.getStatements());
    }

    public StatementListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        if (delEnd == Difference.NONE) {
            return new StatementListDifferenceAdd(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
        }
        else if (addEnd == Difference.NONE) {
            return new StatementListDifferenceDelete(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
        }
        else {
            return new StatementListDifferenceChange(fromStatements, toStatements, delStart, delEnd, addStart, addEnd);
        }
    }
}
