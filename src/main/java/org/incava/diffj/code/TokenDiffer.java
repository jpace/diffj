package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diff.Differ;
import org.incava.diff.Difference;

public class TokenDiffer extends Differ<Token, TokenDifference> {
    public TokenDiffer(List<Token> fromTokens, List<Token> toTokens) {
        super(fromTokens, toTokens, new TokenComparator());
    }

    public TokenDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        if (delEnd == Difference.NONE) {
            return new TokenDifferenceAdd(delStart, delEnd, addStart, addEnd);
        }
        else if (addEnd == Difference.NONE) {
            return new TokenDifferenceDelete(delStart, delEnd, addStart, addEnd);
        }
        else {
            return new TokenDifferenceChange(delStart, delEnd, addStart, addEnd);
        }
    }
}
