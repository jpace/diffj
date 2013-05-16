package org.incava.diffj.type;

import org.incava.analysis.FileDiff;
import org.incava.diffj.*;
import org.incava.ijdk.text.Location;

public class TestTypes extends ItemsTest {
    public TestTypes(String name) {
        super(name);
    }

    protected FileDiff makeTypeRef(String from, String to, Location fromStart, Location fromEnd, Location toStart, Location toEnd) {
        return makeRef(from, to, TYPES_MSGS, fromStart, fromEnd, toStart, toEnd);
    }

    public void testAllTypesAdded() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(1, 1), loc(1, 1), loc(2, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(1, 1), loc(3, 1), loc(4, 1)));

        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(1, 16), loc(2, 1), loc(3, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(1, 16), loc(4, 1), loc(5, 1)));

        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeRef(null, "Test",  loc(1, 1), loc(2, 1), loc(2, 1), loc(3, 1)),
                 makeTypeRef(null, "Test2", loc(1, 1), loc(2, 1), loc(4, 1), loc(5, 1)));
    }

    public void testAllTypesRemoved() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeTypeRef("Test",  null, loc(1, 1), loc(2, 1), loc(1, 1), loc(1, 1)),
                 makeTypeRef("Test2", null, loc(3, 1), loc(4, 1), loc(1, 1), loc(1, 1)));
    }
}
