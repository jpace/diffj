package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDiffer extends Differ<Statement, StatementListDifference> {
    public static List<TokenList> getTokenLists(List<Statement> stmts) {
        StatementList sl = new StatementList(stmts);
        return sl.getTokenLists();
    }

    private final List<Statement> fromStatements;
    private final List<Statement> toStatements;
    
    public StatementListDiffer(Block fromBlock, Block toBlock) {
        super(fromBlock.getStatements(), toBlock.getStatements());
        this.fromStatements = fromBlock.getStatements();
        this.toStatements = toBlock.getStatements();
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
