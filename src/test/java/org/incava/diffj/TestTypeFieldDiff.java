package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;


public class TestTypeFieldDiff extends AbstractTestItemDiff {

    protected final static String[] FIELD_MSGS = TestTypeDiff.FIELD_MSGS;

    public TestTypeFieldDiff(String name) {
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
                 
                 makeRef(null, "i", FIELD_MSGS, loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 10)));
    }

    public void testClassOneFieldRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeRef("i", null, FIELD_MSGS, loc(2, 5), loc(2, 10), loc(1, 1), loc(3, 1)));
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
                 
                 makeRef(null, "j",  FIELD_MSGS, loc(1, 1), loc(4,  1), loc(2, 5), loc(2, 13)),
                 makeRef("i",  null, FIELD_MSGS, loc(2, 5), loc(2, 10), loc(1, 1), loc(4,  1)));
    }
}
