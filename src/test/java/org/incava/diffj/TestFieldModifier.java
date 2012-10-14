package org.incava.diffj;

public class TestFieldModifier extends ItemsTest {
    public TestFieldModifier(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testModifierAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    static int i;",
                           "}"),
                 
                 makeModifierRef(null, "static", loc(2, 5), loc(2, 7), loc(3, 5), loc(3, 10)));
    }

    public void testModifierRemoved() {
        evaluate(new Lines("class Test {",
                           "    final int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeModifierRef("final", null, loc(2, 5), loc(2, 9), loc(3, 5), loc(3, 7)));
    }
}
