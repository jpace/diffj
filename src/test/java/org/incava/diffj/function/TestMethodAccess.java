package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

public class TestMethodAccess extends ItemsTest {
    public TestMethodAccess(String name) {
        super(name);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessAddedRef(locrg(2, 5, 8), locrg(3, 5, 10), "public"));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public void foo() {}",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),

                 makeAccessRemovedRef(locrg(2, 5, 10), locrg(3, 5, 8), "public"));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public void foo() {}",
                           "}"),
                 
                 makeAccessChangedRef(loc(2, 5), loc(3, 5), "private", "public"));
    }
}
