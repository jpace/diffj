package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;

public class TokenListDiffer extends Differ<TokenList, TokenListDifference> {
    public TokenListDiffer(List<TokenList> fromTokenLists, List<TokenList> toTokenLists) {
        super(fromTokenLists, toTokenLists);
    }

    public TokenListDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        if (delEnd == Difference.NONE) {
            return new TokenListDifferenceAdd(delStart, delEnd, addStart, addEnd);
        }
        else if (addEnd == Difference.NONE) {
            return new TokenListDifferenceDelete(delStart, delEnd, addStart, addEnd);
        }
        else {
            return new TokenListDifferenceChange(delStart, delEnd, addStart, addEnd);
        }
    }
}
