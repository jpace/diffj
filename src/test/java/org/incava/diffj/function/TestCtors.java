package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestCtors extends ItemsTest {
    public TestCtors(String name) {
        super(name);
    }

    protected FileDiff makeConstructorAddedRef(LocationRange fromLoc, LocationRange toLoc, String added) {
        return makeRef(fromLoc, toLoc, CONSTRUCTOR_MSGS, null, added);
    }

    protected FileDiff makeConstructorRemovedRef(LocationRange fromLoc, LocationRange toLoc, String removed) {
        return makeRef(fromLoc, toLoc, CONSTRUCTOR_MSGS, removed, null);
    }

    public void testClassConstructorAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s) {}",
                           "}"),

                 makeConstructorAddedRef(locrg(1, 1, 3, 1), locrg(3, 5, 21), "Test(String)"));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeConstructorRemovedRef(locrg(3, 12, 44), locrg(1, 1, 3, 1), "Test(int, double, float)"));
    }
}
