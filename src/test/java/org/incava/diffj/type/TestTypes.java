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

    protected FileDiff makeTypeRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, TYPES_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    protected FileDiff makeTypeRef(String from, String to, LocationRange fromLoc, LocationRange toLoc) {
        return makeRef(from, to, TYPES_MSGS, fromLoc, toLoc);
    }

    public void testAllTypesAdded() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  locrg(1, 1, 1), locrg(1, 1, 2, 1)),
                 makeTypeRef(null, "Test2", locrg(1, 1, 1), locrg(3, 1, 4, 1)));

        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  locrg(1, 1, 16), locrg(2, 1, 3, 1)),
                 makeTypeRef(null, "Test2", locrg(1, 1, 16), locrg(4, 1, 5, 1)));

        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  locrg(1, 1, 2, 1), locrg(2, 1, 3, 1)),
                 makeTypeRef(null, "Test2", locrg(1, 1, 2, 1), locrg(4, 1, 5, 1)));
    }

    public void testAllTypesRemoved() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeTypeRef("Test",  null, locrg(1, 1, 2, 1), locrg(1, 1, 1)),
                 makeTypeRef("Test2", null, locrg(3, 1, 4, 1), locrg(1, 1, 1)));
    }
}
