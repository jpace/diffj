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

    protected FileDiff makePackageRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeRef(fromLoc, toLoc, PACKAGE_MSGS, from, to);
    }

    protected FileDiff makePackageRef(Location fromStart, Location toStart, String from, String to) {
        return makePackageRef(locrg(fromStart, from), locrg(toStart, to), from, to);
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

                 makePackageRef(locrg(1, 9, 22), locrg(4, 1, 5, 1), "org.incava.foo", null));
    }

    public void testPackageAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageRef(locrg(1, 1, 2, 1), locrg(1, 9, 22), null, "org.incava.foo"));
    }

    public void testPackageRenamed() {
        evaluate(new Lines("package org.incava.bar;",
                           "class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageRef(loc(1, 9), loc(1, 9), "org.incava.bar", "org.incava.foo"));
    }
}
