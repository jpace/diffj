package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * A reference to code added, associated with a file by a starting and ending
 * position.
 */
public class FileDiffAdd extends FileDiff {
    public FileDiffAdd(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(Type.ADDED, message, fromLoc, toLoc);
    }

    public FileDiffAdd(String message, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        super(Type.ADDED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public FileDiffAdd(String message, Token from, Token to) {
        super(Type.ADDED, message, from, to);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffAdd(String message, Token fromStart, Token fromEnd, Token toStart, Token toEnd) {
        super(Type.ADDED, message, fromStart, fromEnd, toStart, toEnd);
    }

    public void printContext(DiffWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
    }
}
