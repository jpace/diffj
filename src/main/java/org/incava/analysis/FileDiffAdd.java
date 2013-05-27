package org.incava.analysis;

import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

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
    
    public FileDiffAdd(LocationRange fromLoc, LocationRange toLoc, Message msg, Object ... params) {
        super(Type.ADDED, fromLoc, toLoc, msg, params);
    }

    public void printContext(DiffWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printTo(sb, this);
    }
}
