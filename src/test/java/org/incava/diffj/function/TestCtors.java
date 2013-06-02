package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.LocationRange;

public class TestCtors extends ItemsTest {
    public TestCtors(String name) {
        super(name);
    }

    protected FileDiff makeConstructorRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeRef(fromLoc, toLoc, CONSTRUCTOR_MSGS, from, to);
    }

    public void testClassConstructorAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s) {}",
                           "}"),

                 makeConstructorRef(locrg(1, 1, 3, 1), locrg(3, 5, 21), null, "Test(String)"));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeConstructorRef(locrg(3, 12, 44), locrg(1, 1, 3, 1), "Test(int, double, float)", null));
    }
}
