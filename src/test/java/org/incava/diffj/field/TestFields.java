package org.incava.diffj.field;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

public class TestFields extends ItemsTest {
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
                 
                 new FileDiffAdd(locrg(1, 1, 3, 1), locrg(3, 5, 3, 10), Field.FIELD_ADDED, "i"));
    }

    public void testClassOneFieldRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 new FileDiffDelete(locrg(2, 5, 2, 10), locrg(1, 1, 3, 1), Field.FIELD_REMOVED, "i"));
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
                 
                 new FileDiffAdd(locrg(1, 1, 4,  1), locrg(2, 5, 2, 13), Field.FIELD_ADDED, "j"),
                 new FileDiffDelete(locrg(2, 5, 2, 10), locrg(1, 1, 4,  1), Field.FIELD_REMOVED, "i"));
    }
}
