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
    
    public FileDiffChange(String message, Location firstStart, Location firstEnd, Location secondStart, Location secondEnd) {
        super(Type.CHANGED, message, firstStart, firstEnd, secondStart, secondEnd);
    }

    public FileDiffChange(String message, Token a, Token b) {
        super(Type.CHANGED, message, a, b);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffChange(String message, Token a0, Token a1, Token b0, Token b1) {
        super(Type.CHANGED, message, a0, a1, b0, b1);
    }

    public void printContext(DiffContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append(DiffWriter.EOLN);
        dw.printTo(sb, this);
    }

    public void printNoContext(DiffNoContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append("---");
        sb.append(DiffWriter.EOLN);        
        dw.printTo(sb, this);
    }
}
