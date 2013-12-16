package org.incava.diffj.field;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

public class TestFieldAccess extends ItemsTest {
    public TestFieldAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    private int i;",
                           "}"),
                 
                 makeAccessAddedRef(locrg(2, 5, 7), locrg(3, 5, 11), "private"));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public int i;",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeAccessRemovedRef(locrg(2, 5, 10), locrg(3, 5, 7), "public"));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public int i;",
                           "}"),
                 
                 makeAccessChangedRef(loc(2, 5), loc(3, 5), "private", "public"));
    }
}
