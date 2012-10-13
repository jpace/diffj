package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.DefaultComparator;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.diff.Diff;
import org.incava.ijdk.util.diff.Difference;

/**
 * A list of tokens. Essentially code, but that class is not yet renamed.
 */
public class TokenList {
    public static class TokenComparator extends DefaultComparator<Token> {
        public int doCompare(Token xt, Token yt) {
            int cmp = xt.kind < yt.kind ? -1 : (xt.kind > yt.kind ? 1 : 0);
            if (cmp == 0) {
                cmp = xt.image.compareTo(yt.image);
            }
            return cmp;
        }
    }

    private final List<Token> tokens;

    public TokenList(List<Token> tokens) {
        this.tokens = tokens;
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
        return new LocationRange(FileDiff.toBeginLocation(startTk), FileDiff.toEndLocation(endTk));
    }    
    
    public Token getStart(int start) {
        Token stToken = ListExt.get(tokens, start);
        if (stToken == null && !tokens.isEmpty()) {
            stToken = ListExt.get(tokens, -1);
            stToken = stToken.next;
        }
        return stToken;
    }
}
