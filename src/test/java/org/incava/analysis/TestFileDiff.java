package org.incava.analysis;

import java.awt.Point;
import junit.framework.TestCase;
import org.incava.qualog.Qualog;


public class TestFileDiff extends TestCase {

    public TestFileDiff(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testIdentical() {
        FileDiff a = new FileDiffChange("msg", new Point(0, 0), new Point(1, 1), new Point(0, 0), new Point(1, 1));
        FileDiff b = a;
        assertEquals("a == b", a, b);
        assertEquals("a.compareTo(b)", 0, a.compareTo(b));
    }

    public void testEqual() {
        FileDiff a = new FileDiffChange("msg", new Point(0, 0), new Point(1, 1), new Point(0, 0), new Point(1, 1));
        FileDiff b = new FileDiffChange("msg", new Point(0, 0), new Point(1, 1), new Point(0, 0), new Point(1, 1));
        assertEquals("a == b", a, b);
        assertEquals("a.compareTo(b)", 0, a.compareTo(b));
    }

    public void testEqualNulls() {
        Point a0 = new Point(0, 0);
        Point a1 = new Point(1, 1);
        Point b0 = new Point(2, 4);
        Point b1 = new Point(9, 8);

        Point[][] points = new Point[][] {
            { a0,   a1,   b0,   b1   },
            { a0,   null, b0,   null },
            { null, a1,   null, b1   },
            { null, null, null, null },
        };
        
        for (int pi = 0; pi < points.length; ++pi) {
            FileDiff a = new FileDiffChange("msg", points[pi][0], points[pi][1], points[pi][2], points[pi][3]);
            FileDiff b = new FileDiffChange("msg", points[pi][0], points[pi][1], points[pi][2], points[pi][3]);
            assertEquals("a == b", a, b);
            assertEquals("a.compareTo(b)", 0, a.compareTo(b));
        }
    }

    public void testNotEqual() {
        FileDiff a = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 2));
        FileDiff b = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 3));
        assertTrue("a != b", !a.equals(b));
        assertTrue("a.compareTo(b)", a.compareTo(b) < 0);

        a = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 3));
        b = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 2));
        assertTrue("a != b", !a.equals(b));
        assertTrue("a.compareTo(b)", a.compareTo(b) > 0);

        a = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 3));
        b = new FileDiffAdd("msg", new Point(1, 0), new Point(0, 1), new Point(0, 0), new Point(0, 2));
        assertTrue("a != b", !a.equals(b));
        assertTrue("a.compareTo(b)", a.compareTo(b) < 0);

        a = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(0, 3));
        b = new FileDiffAdd("msg", new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 2));
        assertTrue("a != b", !a.equals(b));
        assertTrue("a.compareTo(b)", a.compareTo(b) > 0);
    }    
}
