package org.incava.test;

import junit.framework.TestCase;
import java.util.*;


public class AbstractTestCaseExt extends TestCase {

    public AbstractTestCaseExt(String name) {
        super(name);
    }

    public AbstractTestCaseExt() {
    }

    static protected String toMessage(String msg) {
        return msg == null ? "" : msg + "; ";
    }

    static public void assertEquals(String msg, Collection expected, Collection actual) {
        String m = toMessage(msg);

        assertEquals(m + "collection sizes", expected.size(), actual.size());
        
        Iterator eit = expected.iterator();
        Iterator ait = actual.iterator();

        int ni = 0;
        while (eit.hasNext() && ait.hasNext()) {
            Object e = eit.next();
            Object a = ait.next();

            assertEquals(m + "collection[" + ni + "]", e, a);
            ++ni;
        }
    }

    static public void assertEquals(Collection expected, Collection actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(String msg, Object[] expected, Object[] actual) {
        String m = toMessage(msg);
        
        assertEquals(m + "array sizes", expected.length, actual.length);

        for (int ni = 0; ni < expected.length; ++ni) {
            assertEquals(m + "array[" + ni + "]", expected[ni], actual[ni]);
        }
    }

    static public void assertEquals(Object[] expected, Object[] actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(int[] expected, int[] actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(String msg, int[] expected, int[] actual) {
        String m = toMessage(msg);
 
        assertEquals(m + "array sizes", expected.length, actual.length);

        for (int ni = 0; ni < expected.length; ++ni) {
            assertEquals(m + "array[" + ni + "]", expected[ni], actual[ni]);
        }
    }
}
