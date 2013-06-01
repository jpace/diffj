package org.incava.diffj.field;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;

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
                 
                 makeAccessRef(locrg(2, 5, 7), locrg(3, 5, 11), null, "private"));
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
                 
                 makeAccessRef(locrg(2, 5, 10), locrg(3, 5, 7), "public", null));
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
                 
                 makeAccessRef(loc(2, 5), loc(3, 5), "private", "public"));
    }
}
