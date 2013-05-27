package org.incava.analysis;

import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

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
    
    public FileDiffDelete(LocationRange fromLoc, LocationRange toLoc, Message msg, Object ... params) {
        super(Type.DELETED, fromLoc, toLoc, msg, params);
    }

    public void printContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
    }
}
