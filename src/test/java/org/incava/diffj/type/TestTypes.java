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

    protected FileDiff makeTypeAddedRef(LocationRange fromLoc, LocationRange toLoc, String added) {
        return makeRef(fromLoc, toLoc, TYPES_MSGS, null, added);
    }

    protected FileDiff makeTypeRemovedRef(LocationRange fromLoc, LocationRange toLoc, String removed) {
        return makeRef(fromLoc, toLoc, TYPES_MSGS, removed, null);
    }

    public void testTypesAddedToEmpty() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeAddedRef(locrg(1, 1, 1), locrg(1, 1, 2, 1), "Test"),
                 makeTypeAddedRef(locrg(1, 1, 1), locrg(3, 1, 4, 1), "Test2"));
    }

    public void testTypesAddedToImports() {
        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeAddedRef(locrg(1, 1, 16), locrg(2, 1, 3, 1), "Test"),
                 makeTypeAddedRef(locrg(1, 1, 16), locrg(4, 1, 5, 1), "Test2"));
    }

    public void testTypesAddedToPackage() {
        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeTypeAddedRef(locrg(1, 1, 2, 1), locrg(2, 1, 3, 1), "Test"),
                 makeTypeAddedRef(locrg(1, 1, 2, 1), locrg(4, 1, 5, 1), "Test2"));
    }

    public void testTypesRemovedToEmpty() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeTypeRef(locrg(1, 1, 2, 1), locrg(1, 1, 1), "Test",  null),
                 makeTypeRef(locrg(3, 1, 4, 1), locrg(1, 1, 1), "Test2", null));
    }

    public void testTypesRemovedToImports() {
        evaluate(new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines("import foo.Bar;"),
                 
                 makeTypeRef(locrg(2, 1, 3, 1), locrg(1, 1, 16), "Test",  null),
                 makeTypeRef(locrg(4, 1, 5, 1), locrg(1, 1, 16), "Test2", null));
    }

    public void testTypesRemovedToPackage() {
        evaluate(new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines("package foo;"),
                 
                 makeTypeRef(locrg(2, 1, 3, 1), locrg(1, 1, 13), "Test",  null),
                 makeTypeRef(locrg(4, 1, 5, 1), locrg(1, 1, 13), "Test2", null));
    }
}
