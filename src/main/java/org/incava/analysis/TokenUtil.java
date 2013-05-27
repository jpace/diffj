package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

public class TokenUtil {
    public static Location toBeginLocation(Token t) {
        return t == null ? null : new Location(t.beginLine, t.beginColumn);
    }

    public static LocationRange toLocationRange(Token from, Token to) {
        return new LocationRange(toBeginLocation(from), toEndLocation(to));
    }
    
    public static Location toEndLocation(Token t) {
        return t == null ? null : new Location(t.endLine, t.endColumn);
    }
}
