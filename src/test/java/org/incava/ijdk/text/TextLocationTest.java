package org.incava.ijdk.text;

import java.util.*;
import junit.framework.TestCase;

public class TextLocationTest extends TestCase {    
    public TextLocationTest(String name) {
        super(name);
    }

    public void testCtor() {
        TextLocation tl = new TextLocation(4, 1, 3);
        assertEquals(4, tl.getPosition());
        assertEquals(1, tl.getLine());
        assertEquals(3, tl.getColumn());
    }

    public void testToString() {
        TextLocation tl = new TextLocation(4, 1, 3);
        assertEquals("[position: 4, line: 1, column: 3]", tl.toString());
    }

    public void testEquals() {
        TextLocation a = new TextLocation(4, 1, 3);
        TextLocation b = new TextLocation(4, 1, 3);
        TextLocation c = new TextLocation(8, 1, 3);
        
        assertTrue(new TextLocation(4, 1, 3).equals(new TextLocation(4, 1, 3)));
        assertTrue(new TextLocation(1, 3, 4).equals(new TextLocation(1, 3, 4)));

        assertFalse(new TextLocation(4, 1, 3).equals(new TextLocation(4, 1, 2)));
        assertFalse(new TextLocation(4, 1, 3).equals(new TextLocation(4, 2, 3)));
        assertFalse(new TextLocation(4, 1, 3).equals(new TextLocation(5, 1, 3)));

        assertFalse(new TextLocation(4, 1, 3).equals(new Integer(4)));
    }
}
