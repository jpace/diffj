package org.incava.analysis;

import org.incava.ijdk.text.LocationRange;

/**
 * Code added.
 */
public class FileDiffCodeAdded extends FileDiffAdd {
    public FileDiffCodeAdded(String message, LocationRange fromLoc, LocationRange toLoc) {
        super(message, fromLoc, toLoc);
    }

    public void printNoContext(DiffWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append("---");
        sb.append(DiffWriter.EOLN);        
        dw.printTo(sb, this);
    }
}
