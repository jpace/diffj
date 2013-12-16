package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

public class TestCtorAccess extends ItemsTest {
    public TestCtorAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public Test() {}",
                           "}"),
                 
                 makeAccessAddedRef(locrg(2, 5, 8), locrg(3, 5, 10), "public"));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() {}",
                           "}"),
                 
                 makeAccessRemovedRef(locrg(2, 5, 10), locrg(3, 5, 8), "public"));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public Test() {}",
                           "}"),
                 
                 makeAccessChangedRef(loc(2, 5), loc(3, 5), "private", "public"));
    }
}
