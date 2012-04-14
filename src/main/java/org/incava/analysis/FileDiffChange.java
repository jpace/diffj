package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * A reference to code changed, associated with a file by a starting and ending
 * position.
 */
public class FileDiffChange extends FileDiff {
    public FileDiffChange(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(Type.CHANGED, message, fromLoc, toLoc);
    }
    
    public FileDiffChange(String message, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        super(Type.CHANGED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public FileDiffChange(String message, Token from, Token to) {
        super(Type.CHANGED, message, from, to);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffChange(String message, Token fromStart, Token fromEnd, Token toStart, Token toEnd) {
        super(Type.CHANGED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public void printContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append(DiffWriter.EOLN);
        dw.printTo(sb, this);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append("---");
        sb.append(DiffWriter.EOLN);        
        dw.printTo(sb, this);
    }
}
