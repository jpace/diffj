package org.incava.analysis;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

/**
 * A reference to code added, associated with a file by a starting and ending
 * position.
 */
public class FileDiffAdd extends FileDiff {
    public FileDiffAdd(String message, Location firstStart, Location firstEnd, Location secondStart, Location secondEnd) {
        super(Type.ADDED, message, firstStart, firstEnd, secondStart, secondEnd);
    }

    public FileDiffAdd(String message, Token a, Token b) {
        super(Type.ADDED, message, a, b);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffAdd(String message, Token a0, Token a1, Token b0, Token b1) {
        super(Type.ADDED, message, a0, a1, b0, b1);
    }

    public void print(DiffContextWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
        sb.append(DiffContextWriter.EOLN);
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
    }
}
