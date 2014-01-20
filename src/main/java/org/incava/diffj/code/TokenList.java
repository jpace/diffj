package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * A list of tokens representing code.
 */
public class TokenList implements Comparable<TokenList> {
    private final List<Token> tokens;    

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
    }

    public TokenList(SimpleNode node) {
        this(SimpleNodeUtil.getChildTokens(node));
    }

    public List<TokenDifference> diff(TokenList toTokenList) {
        TokenDiffer td = new TokenDiffer(tokens, toTokenList.tokens);
        return td.execute();
    }
    
    public void add(TokenList tokenList) {
        tokens.addAll(tokenList.tokens);
    }

    private LocationRange getLocationRange(Token startTk, Token endTk) {
        Tkn startTkn = new Tkn(startTk);
        Tkn endTkn = new Tkn(endTk);
        return startTkn.getLocationRange(endTkn);
    }    

    public LocationRange getLocationRange(Integer start, Integer end) {
        Token startTk = getStart(start);
        Token endTk = end == Difference.NONE ? startTk : tokens.get(end);
        return getLocationRange(startTk, endTk);
    }    

    public LocationRange getTokenLocationRange(Integer idx) {
        Token tk = getStart(idx);
        return getLocationRange(tk, tk);
    }    

    public Token get(int idx) {
        return ListExt.get(tokens, idx);
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

    public int compareTo(TokenList other) {
        int cmp = new Integer(tokens.size()).compareTo(other.tokens.size());
        if (cmp != 0) {
            return cmp;
        }

        for (int idx = 0; idx < tokens.size(); ++idx) {
            Token xt = tokens.get(idx);
            Token yt = other.tokens.get(idx);
            Tkn x = new Tkn(xt);
            Tkn y = new Tkn(yt);
            cmp = x.compareTo(y);
            if (cmp != 0) {
                return cmp;
            }
        }

        return cmp;
    }
}
