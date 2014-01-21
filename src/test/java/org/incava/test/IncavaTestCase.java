package org.incava.test;

import junit.framework.TestCase;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class IncavaTestCase extends TestCase {
    public IncavaTestCase(String name) {
        super(name);
    }

    public IncavaTestCase() {
    }

    static protected String toMessage(String msg) {
        return msg == null ? "" : msg + "; ";
    }

    static public void assertEquals(String msg, Collection<?> expected, Collection<?> actual) {
        String m = toMessage(msg);

        int count = Math.max(expected.size(), actual.size());
        Iterator<?> eit = expected.iterator();
        Iterator<?> ait = actual.iterator();

        for (int ni = 0; ni < count; ++ni) {
            Object e = eit.next();
            Object a = ait.next();

            assertEquals(m + "collection[" + ni + "]", e, a);
        }
    }

    static public void assertEquals(Collection<?> expected, Collection<?> actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(String msg, Object[] expected, Object[] actual) {
        assertEquals(msg, Arrays.asList(expected), Arrays.asList(actual));
    }

    static public void assertEquals(Object[] expected, Object[] actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(int[] expected, int[] actual) {
        assertEquals(null, expected, actual);
    }

    static public void assertEquals(String msg, int[] expected, int[] actual) {
        String m = toMessage(msg);
        for (int ni = 0; ni < Math.max(expected.length, actual.length); ++ni) {
            assertEquals(m + "array[" + ni + "]", expected[ni], actual[ni]);
        }
    }
}
