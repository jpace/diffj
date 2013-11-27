package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.TokenUtil;
import org.incava.diffj.element.Tkn;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.DefaultComparator;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * A list of tokens representing code.
 */
public class TokenList {
    public static class TokenComparator extends DefaultComparator<Token> {
        public int doCompare(Token xt, Token yt) {
            return compareTokens(xt, yt);
        }
    }

    public static class TokenListComparator extends DefaultComparator<TokenList> {
        public int doCompare(TokenList xList, TokenList yList) {
            int cmp = new Integer(xList.tokens.size()).compareTo(yList.tokens.size());
            if (cmp != 0) {
                return cmp;
            }

            for (int idx = 0; idx < xList.tokens.size(); ++idx) {
                Token xt = xList.tokens.get(idx);
                Token yt = yList.tokens.get(idx);
                cmp = compareTokens(xt, yt);
                if (cmp != 0) {
                    return cmp;
                }
            }

            return cmp;
        }
    }

    public static int compareTokens(Token xt, Token yt) {
        Tkn x = new Tkn(xt);
        Tkn y = new Tkn(yt);
        return x.compareTo(y);
    }

    private final List<Token> tokens;

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TokenList(SimpleNode node) {
        tokens = SimpleNodeUtil.getChildTokens(node);
    }

    public Differ<Token, TokenDifference> diff(TokenList toTokenList) {
        return new Differ<Token, TokenDifference>(tokens, toTokenList.tokens, new TokenComparator()) {
            public TokenDifference createDifference(Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
                if (delEnd == Difference.NONE) {
                    return new TokenDifference(delStart, delEnd, addStart, addEnd);
                }
                else if (addEnd == Difference.NONE) {
                    return new TokenDifference(delStart, delEnd, addStart, addEnd);
                }
                else {
                    return new TokenDifference(delStart, delEnd, addStart, addEnd);
                }
            }
        };
    }

    public LocationRange getLocationRange(Integer start, Integer end) {
        Token startTk = getStart(start);
        Token endTk = end == Difference.NONE ? startTk : tokens.get(end);
        Tkn startTkn = new Tkn(startTk);
        Tkn endTkn = new Tkn(endTk);
        return new LocationRange(startTkn.getBeginLocation(), endTkn.getEndLocation());
    }    
    
    public Token getStart(int start) {
        Token stToken = ListExt.get(tokens, start);
        if (stToken == null && !tokens.isEmpty()) {
            stToken = ListExt.get(tokens, -1);
            stToken = stToken.next;
        }
        return stToken;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Token tk : tokens) {
            sb.append("^").append(tk.image);
        }
        return sb.toString();
    }
}
