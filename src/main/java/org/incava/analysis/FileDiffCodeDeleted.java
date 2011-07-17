package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;


/**
 * Code deleted.
 */
public class FileDiffCodeDeleted extends FileDiffDelete {

    public FileDiffCodeDeleted(String message, Point firstStart, Point firstEnd, Point secondStart, Point secondEnd) {
        super(message, firstStart, firstEnd, secondStart, secondEnd);
    }

    public FileDiffCodeDeleted(String message, Token a, Token b) {
        super(message, a, b);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffCodeDeleted(String message, Token a0, Token a1, Token b0, Token b1) {
        super(message, a0, a1, b0, b1);
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);

        sb.append("---");
        sb.append(DiffNoContextWriter.EOLN);
        
        dw.printTo(sb, this);
    }
}
