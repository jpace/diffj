package org.incava.diffj;

public class TestTypesDiff extends AbstractTestItemDiff {
    protected final static String[] TYPES_MSGS = new String[] {
        TypesDiff.TYPE_DECLARATION_REMOVED,
        null,
        TypesDiff.TYPE_DECLARATION_ADDED,
    };
    
    public TestTypesDiff(String name) {
        super(name);
    }

    public void testAllTypesAdded() {
        evaluate(new Lines(""),

                 new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeRef(null, "Test",  TYPES_MSGS, loc(1, 1), loc(1, 1), loc(1, 1), loc(2, 1)),
                 makeRef(null, "Test2", TYPES_MSGS, loc(1, 1), loc(1, 1), loc(3, 1), loc(4, 1)));

        evaluate(new Lines("import foo.Bar;"),

                 new Lines("import foo.Bar;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeRef(null, "Test",  TYPES_MSGS,  loc(1, 1), loc(1, 16), loc(2, 1), loc(3, 1)),
                 makeRef(null, "Test2", TYPES_MSGS,  loc(1, 1), loc(1, 16), loc(4, 1), loc(5, 1)));

        evaluate(new Lines("package foo;",
                           ""),

                 new Lines("package foo;",
                           "class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),
                 
                 makeRef(null, "Test",  TYPES_MSGS, loc(1, 1), loc(2, 1), loc(2, 1), loc(3, 1)),
                 makeRef(null, "Test2", TYPES_MSGS, loc(1, 1), loc(2, 1), loc(4, 1), loc(5, 1)));
    }

    public void testAllTypesRemoved() {
        evaluate(new Lines("class Test {",
                           "}",
                           "interface Test2 {",
                           "}"),

                 new Lines(""),
                 
                 makeRef("Test",  null, TYPES_MSGS, loc(1, 1), loc(2, 1), loc(1, 1), loc(1, 1)),
                 makeRef("Test2", null, TYPES_MSGS, loc(3, 1), loc(4, 1), loc(1, 1), loc(1, 1)));
    }

    protected String typeDeclMsg(String from, String to) {
        return getMessage(TypesDiff.TYPE_DECLARATION_REMOVED,
                          TypesDiff.TYPE_DECLARATION_ADDED,
                          null, // no changed message
                          from, to);
    }
}
