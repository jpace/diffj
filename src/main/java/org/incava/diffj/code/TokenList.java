package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.TokenUtil;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.DefaultComparator;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Diff;
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
        int cmp = xt.kind < yt.kind ? -1 : (xt.kind > yt.kind ? 1 : 0);
        if (cmp == 0) {
            cmp = xt.image.compareTo(yt.image);
        }
        return cmp;
    }

    private final List<Token> tokens;

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TokenList(SimpleNode node) {
        tokens = SimpleNodeUtil.getChildTokens(node);
    }

    public Diff<Token> diff(TokenList toTokenList) {
        return new Diff<Token>(tokens, toTokenList.tokens, new TokenComparator());
    }

    public LocationRange getLocationRange(Integer start, Integer end) {
        Token startTk, endTk;
        if (end == Difference.NONE) {
            endTk = startTk = getStart(start);
        }
        else {
            startTk = tokens.get(start);
            endTk = tokens.get(end);
        }
        return new LocationRange(TokenUtil.toBeginLocation(startTk), TokenUtil.toEndLocation(endTk));
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
        tr.Ace.log("tokens", tokens);
        StringBuffer sb = new StringBuffer();
        for (Token tk : tokens) {
            sb.append("^").append(tk.image);
        }
        tr.Ace.cyan("sb", sb);
        return sb.toString();
    }
}
