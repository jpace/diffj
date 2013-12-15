package org.incava.diffj.code;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * Wraps a PMD Token
 */
public class Tkn {
    private final Token token;

    public Tkn(Token token) {
        this.token = token;
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
        return token == null ? null : new Location(token.endLine, token.endColumn);
    }

    public int compareTo(Tkn other) {
        if (token == null) {
            return other.token == null ? 0 : -1;
        }
        else if (other.token == null) {
            return 1;
        }
        
        int cmp = new Integer(token.kind).compareTo(new Integer(other.token.kind));
        if (cmp == 0) {
            cmp = token.image.compareTo(other.token.image);
        }
        return cmp;
    }
}
