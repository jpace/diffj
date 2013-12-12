package org.incava.diffj.code;

import java.util.List;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class TokenListDiffer extends Differ<TokenList, TokenListDifference> {
    private final List<TokenList> fromTokenLists;
    private final List<TokenList> toTokenLists;
    
    public TokenListDiffer(List<TokenList> fromTokenLists, List<TokenList> toTokenLists) {
        super(fromTokenLists, toTokenLists);
        this.fromTokenLists = fromTokenLists;
        this.toTokenLists = toTokenLists;
    }

    public TokenListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        if (delEnd == Difference.NONE) {
            return new TokenListDifferenceAdd(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
        else if (addEnd == Difference.NONE) {
            return new TokenListDifferenceDelete(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
        else {
            return new TokenListDifferenceChange(fromTokenLists, toTokenLists, delStart, delEnd, addStart, addEnd);
        }
    }
}
