package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.*;
import org.incava.ijdk.text.Location;

public class TestCtors extends ItemsTest {
    public TestCtors(String name) {
        super(name);
    }

    protected FileDiff makeConstructorRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, CONSTRUCTOR_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    public void testClassConstructorAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s) {}",
                           "}"),

                 makeConstructorRef(null, "Test(String)", loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 21)));
    }

    public void testClassConstructorRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    public Test(int i, double d, float f) {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeConstructorRef("Test(int, double, float)", null, loc(3, 12), loc(3, 44), loc(1, 1), loc(3, 1)));
    }
}
