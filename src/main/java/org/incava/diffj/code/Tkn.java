package org.incava.diffj.code;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.pmdx.TokenUtil;

/**
 * Wraps a PMD Token
 */
public class Tkn {
    private final Token token;

    public Tkn(Token token) {
        this.token = token;
        // tr.Ace.yellow("token          ", token);
        // tr.Ace.yellow("token.image    ", token == null ? null : token.image);
        // tr.Ace.yellow("token.endLine  ", token == null ? null : token.endLine);
        // tr.Ace.yellow("token.endColumn", token == null ? null : token.endColumn);
    }

    public Token getToken() {
        return token;
    }

    public Location getBeginLocation() {
        return token == null ? null : new Location(token.beginLine, token.beginColumn);
    }

    public LocationRange getLocationRange(Tkn to) {
        return new LocationRange(getBeginLocation(), to.getEndLocation());
    }
    
    public Location getEndLocation() {
        // tr.Ace.log("token          ", token);
        // tr.Ace.log("token.endLine  ", token == null ? null : token.endLine);
        // tr.Ace.log("token.endColumn", token == null ? null : token.endColumn);
        return token == null ? null : new Location(token.endLine, token.endColumn);
    }

    public int compareTo(Tkn other) {
        int cmp = token.kind < other.token.kind ? -1 : (token.kind > other.token.kind ? 1 : 0);
        if (cmp == 0) {
            cmp = token.image.compareTo(other.token.image);
        }
        return cmp;
    }
}
