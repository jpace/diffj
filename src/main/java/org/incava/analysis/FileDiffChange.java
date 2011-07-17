package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;


/**
 * A reference to code changed, associated with a file by a starting and ending
 * position.
 */
public class FileDiffChange extends FileDiff {

    public FileDiffChange(String message, Point firstStart, Point firstEnd, Point secondStart, Point secondEnd) {
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

    public void print(DiffContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append(DiffContextWriter.EOLN);
        
        dw.printTo(sb, this);
        sb.append(DiffContextWriter.EOLN);
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);

        sb.append("---");
        sb.append(DiffNoContextWriter.EOLN);
        
        dw.printTo(sb, this);
    }
}
