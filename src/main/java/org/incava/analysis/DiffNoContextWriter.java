package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
import org.incava.qualog.Qualog;
import org.incava.ijdk.util.ANSI;


/**
 * Writes differences. Actually returns the differences as strings. Writing is
 * left to the invoker.
 */
public class DiffNoContextWriter extends DiffWriter {

    public DiffNoContextWriter(String[] fromContents, String[] toContents) {
        super(fromContents, toContents);
    }

    protected void printFrom(StringBuilder sb, FileDiff ref) {
        Point del = new Point(ref.firstStart.x,  ref.firstEnd.x);
        printLines(sb, del, "<", fromContents);
    }

    protected void printTo(StringBuilder sb, FileDiff ref) {
        Point add = new Point(ref.secondStart.x, ref.secondEnd.x);
        printLines(sb, add, ">", toContents);
    }

    protected void printLines(StringBuilder sb, FileDiff ref) {
        ref.print(this, sb);
        sb.append(EOLN);
    }

    protected void printLines(StringBuilder sb, Point pt, String ind, String[] lines) {
        for (int lnum = pt.x; lnum <= pt.y; ++lnum) {
            sb.append(ind + " " + lines[lnum - 1]);
            sb.append(EOLN);
        }
    }
}
