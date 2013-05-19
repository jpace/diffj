package org.incava.diffj.field;

import org.incava.analysis.FileDiff;
import org.incava.diffj.*;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class TestFields extends ItemsTest {
    protected final static Message[] FIELD_MSGS = new Message[] {
        Field.FIELD_REMOVED,
        null,
        Field.FIELD_ADDED,
    };

    public TestFields(String name) {
        super(name);
    }

    public void testClassOneFieldAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeFieldRef(null, "i", locrg(1, 1, 3, 1), locrg(3, 5, 3, 10)));
    }

    public void testClassOneFieldRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeFieldRef("i", null, locrg(2, 5, 2, 10), locrg(1, 1, 3, 1)));
    }

    public void testClassOneFieldRemovedOneFieldAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    String j;",
                           "",
                           "}"),
                 
                 makeFieldRef(null, "j",  locrg(1, 1, 4,  1), locrg(2, 5, 2, 13)),
                 makeFieldRef("i",  null, locrg(2, 5, 2, 10), locrg(1, 1, 4,  1)));
    }

    protected FileDiff makeFieldRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, FIELD_MSGS, fromLoc, toLoc);
    }
}
