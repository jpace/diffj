package org.incava.diffj.function;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

public class TestPackage extends ItemsTest {
    public TestPackage(String name) {
        super(name);
    }

    protected FileDiff makePackageRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, PACKAGE_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makePackageRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, PACKAGE_MSGS, fromLoc, toLoc);
    }

    protected FileDiff makePackageRef(String from, String to, Location fromStart, Location toStart) {
        return makeRef(from, to, PACKAGE_MSGS, 
                       fromStart, loc(fromStart, from),
                       toStart,   loc(toStart,   to));
    }

    public void testPackageNoChange() {
        evaluate(new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testPackageNone() {
        evaluate(new Lines("class Test {",
                           "}"),
                 
                 new Lines("class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testPackageRemoved() {
        evaluate(new Lines("package org.incava.foo;",
                           "", 
                           "/**", 
                           " * This is a test class.",
                           " */",
                           "class Test {",
                           "}"),

                 new Lines("/**", 
                           " * This is a test class.",
                           " */",
                           "class Test {",
                           "}"),

                 makePackageRef("org.incava.foo", null, locrg(1, 9, 22), locrg(4, 1, 5, 1)));
    }

    public void testPackageAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageRef(null, "org.incava.foo", locrg(1, 1, 2, 1), locrg(1, 9, 22)));
    }

    public void testPackageRenamed() {
        evaluate(new Lines("package org.incava.bar;",
                           "class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageRef("org.incava.bar", "org.incava.foo", loc(1, 9), loc(1, 9)));
    }
}
