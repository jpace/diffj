package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.Token;


/**
 * A reference to code deleted, associated with a file by a starting and ending
 * position. Note that code deleted is not the same as a section of code deleted.
 *
 * @todo fix the names.
 */
public class FileDiffDelete extends FileDiff {

    public FileDiffDelete(String message, Point firstStart, Point firstEnd, Point secondStart, Point secondEnd) {
        super(Type.DELETED, message, firstStart, firstEnd, secondStart, secondEnd);
    }

    public FileDiffDelete(String message, Token a, Token b) {
        super(Type.DELETED, message, a, b);
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiffDelete(String message, Token a0, Token a1, Token b0, Token b1) {
        super(Type.DELETED, message, a0, a1, b0, b1);
    }

    public void print(DiffContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
        sb.append(DiffContextWriter.EOLN);
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
        dw.printFrom(sb, this);
    }
}
