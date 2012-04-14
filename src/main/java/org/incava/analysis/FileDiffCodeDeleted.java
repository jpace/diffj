package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * Code deleted.
 */
public class FileDiffCodeDeleted extends FileDiffDelete {
    public FileDiffCodeDeleted(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(message, fromLoc, toLoc);
    }

    public FileDiffCodeDeleted(String message, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        super(message, fromStart, fromEnd, toStart, toEnd);
    }

    public FileDiffCodeDeleted(String message, Token from, Token to) {
        super(message, from, to);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffCodeDeleted(String message, Token fromStart, Token fromEnd, Token toStart, Token toEnd) {
        super(message, fromStart, fromEnd, toStart, toEnd);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append("---");
        sb.append(DiffWriter.EOLN);        
        dw.printTo(sb, this);
    }
}
