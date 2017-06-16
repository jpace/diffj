package org.incava.ijdk.text;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

public class TextRangeTest extends TestCase {    
    public TextRangeTest(String name) {
        super(name);
    }

    public void testCtor() {
        // TextRange tl = new TextRange(4, 1, 3);
        // assertEquals(4, tl.getPosition());
        // assertEquals(1, tl.getLine());
        // assertEquals(3, tl.getColumn());
    }

    public void testToString() {
        // TextRange tl = new TextRange(4, 1, 3);
        // assertEquals("[position: 4, line: 1, column: 3]", tl.toString());
    }

    public void testEquals() {
        // TextRange a = new TextRange(4, 1, 3);
        // TextRange b = new TextRange(4, 1, 3);
        // TextRange c = new TextRange(8, 1, 3);
        
        // assertTrue(new TextRange(4, 1, 3).equals(new TextRange(4, 1, 3)));
        // assertTrue(new TextRange(1, 3, 4).equals(new TextRange(1, 3, 4)));

        // assertFalse(new TextRange(4, 1, 3).equals(new TextRange(4, 1, 2)));
        // assertFalse(new TextRange(4, 1, 3).equals(new TextRange(4, 2, 3)));
        // assertFalse(new TextRange(4, 1, 3).equals(new TextRange(5, 1, 3)));

        // assertFalse(new TextRange(4, 1, 3).equals(new Integer(4)));
    }
}
