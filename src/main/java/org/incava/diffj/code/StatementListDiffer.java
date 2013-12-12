package org.incava.diffj.code;

import java.util.List;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class StatementListDiffer extends Differ<TokenList, StatementListDifference> {
    private final List<TokenList> fromTokenLists;
    private final List<TokenList> toTokenLists;
    
    public StatementListDiffer(List<TokenList> fromTokenLists, List<TokenList> toTokenLists) {
        super(fromTokenLists, toTokenLists);
        this.fromTokenLists = fromTokenLists;
        this.toTokenLists = toTokenLists;
    }

    public StatementListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        if (delEnd == Difference.NONE) {
            return new StatementListDifferenceAdd(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
        else if (addEnd == Difference.NONE) {
            return new StatementListDifferenceDelete(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
        else {
            return new StatementListDifferenceChange(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
    }
}
