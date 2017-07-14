package org.incava.diffj.function;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.compunit.Package;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

public class PackageTest extends ItemsTest {
    public PackageTest(String name) {
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

                 new FileDiffDelete(locrg(1, 9, 22), locrg(4, 1, 5, 1), Package.PACKAGE_REMOVED, "org.incava.foo"));
    }

    public void testPackageAdded() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),
                 
                 new FileDiffAdd(locrg(1, 1, 2, 1), locrg(1, 9, 22), Package.PACKAGE_ADDED, "org.incava.foo"));
    }

    public void testPackageRenamed() {
        String oldPkg = "org.incava.bar";
        String newPkg = "org.incava.foo";

        evaluate(new Lines("package org.incava.bar;",
                           "class Test {",
                           "}"),

                 new Lines("package org.incava.foo;",
                           "", 
                           "class Test {",
                           "}"),

                 new FileDiffChange(locrg(loc(1, 9), oldPkg), locrg(loc(1, 9), newPkg), Package.PACKAGE_RENAMED, oldPkg, newPkg));
    }
}
