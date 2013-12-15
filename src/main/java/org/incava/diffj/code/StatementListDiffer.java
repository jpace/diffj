package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDiffer extends Differ<Statement, StatementListDifference> {
    public static List<TokenList> getTokenLists(List<Statement> stmts) {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : stmts) {
            tokenLists.add(stmt.getTokenList());
        }
        return tokenLists;
    }

    private final List<Statement> fromStatements;
    private final List<Statement> toStatements;
    
    public StatementListDiffer(Block fromBlock, Block toBlock) {
        super(fromBlock.getStatements(), toBlock.getStatements());
        this.fromStatements = fromBlock.getStatements();
        this.toStatements = toBlock.getStatements();
    }

    public List<TokenList> getFromTokenLists() {
        return getTokenLists(fromStatements);
    }

    public List<TokenList> getToTokenLists() {
        return getTokenLists(toStatements);
    }

    public StatementListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        List<TokenList> fromLists = getFromTokenLists();
        List<TokenList> toLists = getToTokenLists();
        
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
