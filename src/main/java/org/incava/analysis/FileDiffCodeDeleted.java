package org.incava.analysis;

import org.incava.ijdk.text.LocationRange;

/**
 * Code deleted.
 */
public class FileDiffCodeDeleted extends FileDiffDelete {
    public FileDiffCodeDeleted(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(message, fromLoc, toLoc);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append("---");
        sb.append(DiffWriter.EOLN);        
        dw.printTo(sb, this);
    }
}
