package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

public class TestTypes extends ItemsTest {
    public TestTypes(String name) {
        super(name);
    }

    protected FileDiff makeTypeRef(LocationRange fromLoc, LocationRange toLoc, String from, String to) {
        return makeRef(fromLoc, toLoc, TYPES_MSGS, from, to);
    }

    public void testAllTypesAdded() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(locrg(1, 1, 1), locrg(1, 1, 2, 1), null, "Test"),
                 makeTypeRef(locrg(1, 1, 1), locrg(3, 1, 4, 1), null, "Test2"));

        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(locrg(1, 1, 16), locrg(2, 1, 3, 1), null, "Test"),
                 makeTypeRef(locrg(1, 1, 16), locrg(4, 1, 5, 1), null, "Test2"));

        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(locrg(1, 1, 2, 1), locrg(2, 1, 3, 1), null, "Test"),
                 makeTypeRef(locrg(1, 1, 2, 1), locrg(4, 1, 5, 1), null, "Test2"));
    }

    public void testAllTypesRemoved() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeTypeRef(locrg(1, 1, 2, 1), locrg(1, 1, 1), "Test",  null),
                 makeTypeRef(locrg(3, 1, 4, 1), locrg(1, 1, 1), "Test2", null));
    }
}
