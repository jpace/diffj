package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * A reference to code deleted, associated with a file by a starting and ending
 * position. Note that code deleted is not the same as a section of code deleted.
 *
 * @todo fix the names.
 */
public class FileDiffDelete extends FileDiff {
    public FileDiffDelete(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(Type.DELETED, message, fromLoc, toLoc);
    }
    
    public FileDiffDelete(String message, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        super(Type.DELETED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public FileDiffDelete(String message, Token from, Token to) {
        super(Type.DELETED, message, from, to);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffDelete(String message, Token fromStart, Token fromEnd, Token toStart, Token toEnd) {
        super(Type.DELETED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public void printContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
    }
}
