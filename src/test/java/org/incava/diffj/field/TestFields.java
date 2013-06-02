package org.incava.diffj.field;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
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
                 
                 makeFieldRef(locrg(1, 1, 3, 1), locrg(3, 5, 3, 10), null, "i"));
    }

    public void testClassOneFieldRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeFieldRef(locrg(2, 5, 2, 10), locrg(1, 1, 3, 1), "i", null));
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
                 
                 makeFieldRef(locrg(1, 1, 4,  1), locrg(2, 5, 2, 13), null, "j"),
                 makeFieldRef(locrg(2, 5, 2, 10), locrg(1, 1, 4,  1), "i",  null));
    }

    protected FileDiff makeFieldRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeRef(fromLoc, toLoc, FIELD_MSGS, from, to);
    }
}
