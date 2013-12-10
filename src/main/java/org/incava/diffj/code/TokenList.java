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
    private final String name;
    
    public TokenList(List<Token> tokens) {
        this(null, tokens);
    }

    public TokenList(SimpleNode node) {
        this(null, node);
    }

    public TokenList(String name, List<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public TokenList(String name, SimpleNode node) {
        this(name, SimpleNodeUtil.getChildTokens(node));
    }

    public List<TokenDifference> diff(TokenList toTokenList) {
        TokenDiffer td = new TokenDiffer(tokens, toTokenList.tokens);
        return td.execute();
    }
    
    public void add(TokenList tokenList) {
        tokens.addAll(tokenList.tokens);
    }

    public LocationRange getLocationRange(Integer start, Integer end) {
        Token startTk = getStart(start);
        Token endTk = end == Difference.NONE ? startTk : tokens.get(end);
        Tkn startTkn = new Tkn(startTk);
        Tkn endTkn = new Tkn(endTk);
        return new LocationRange(startTkn.getBeginLocation(), endTkn.getEndLocation());
    }    

    /**
     * Returns a location range, accepting negative indices, which go from the
     * end.
     */
    public LocationRange fetchLocationRange(Integer start, Integer end) {
        Token startTk = getStart(start);
        Token endTk = ListExt.get(tokens, end);
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
