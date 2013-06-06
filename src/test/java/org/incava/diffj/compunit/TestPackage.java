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

    protected FileDiff makePackageAddedRef(LocationRange fromLoc, LocationRange toLoc, String added) {
        return makeRef(fromLoc, toLoc, PACKAGE_MSGS, null, added);
    }

    protected FileDiff makePackageRemovedRef(LocationRange fromLoc, LocationRange toLoc, String removed) {
        return makeRef(fromLoc, toLoc, PACKAGE_MSGS, removed, null);
    }

    protected FileDiff makePackageChangedRef(Location fromStart, Location toStart, String from, String to) {
        return makeRef(locrg(fromStart, from), locrg(toStart, to), PACKAGE_MSGS, from, to);
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

                 makePackageRemovedRef(locrg(1, 9, 22), locrg(4, 1, 5, 1), "org.incava.foo"));
    }

    public void testPackageAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageAddedRef(locrg(1, 1, 2, 1), locrg(1, 9, 22), "org.incava.foo"));
    }

    public void testPackageRenamed() {
        evaluate(new Lines("package org.incava.bar;",
                           "class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageChangedRef(loc(1, 9), loc(1, 9), "org.incava.bar", "org.incava.foo"));
    }
}
