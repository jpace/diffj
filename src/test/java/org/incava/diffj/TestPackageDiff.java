package org.incava.diffj;

import java.awt.Point;
import java.text.MessageFormat;
import org.incava.analysis.FileDiff;


public class TestPackageDiff extends AbstractTestItemDiff {

    protected final static String[] PACKAGE_MSGS = new String[] {
        PackageDiff.PACKAGE_REMOVED, 
        PackageDiff.PACKAGE_RENAMED,
        PackageDiff.PACKAGE_ADDED,
    };

    public TestPackageDiff(String name) {
        super(name);
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

                 makePackageRef("org.incava.foo", null, loc(1, 9), loc(1, 22), loc(4, 1), loc(5, 1)));
    }

    public void testPackageAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 makePackageRef(null, "org.incava.foo", loc(1, 1), loc(2, 1), loc(1, 9), loc(1, 22)));
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

    protected FileDiff makePackageRef(String from, String to,
                                           Point fromStart, Point fromEnd,
                                           Point toStart, Point toEnd) {
        return makeRef(from, to, PACKAGE_MSGS, 
                       fromStart, fromEnd,
                       toStart, toEnd);
    }

    protected FileDiff makePackageRef(String from, String to,
                                           Point fromStart, 
                                           Point toStart) {
        return makeRef(from, to, PACKAGE_MSGS, 
                       fromStart, loc(fromStart, from),
                       toStart,   loc(toStart ,  to));
    }

}
