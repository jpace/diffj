package org.incava.analysis;

import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

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

    public FileDiffChange(LocationRange fromLoc, LocationRange toLoc, Message msg, Object ... params) {
        super(Type.CHANGED, fromLoc, toLoc, msg, params);
    }

    /**
     * Expands a file diff for the given ranges.
     */
    public FileDiffChange(String message, FileDiff fileDiff, LocationRange fromLocRg, LocationRange toLocRg) {
        this(message, fileDiff.getFirstLocation().getStart(), fromLocRg.getEnd(), fileDiff.getSecondLocation().getStart(), toLocRg.getEnd());
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
