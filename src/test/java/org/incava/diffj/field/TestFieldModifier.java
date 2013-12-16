package org.incava.diffj.field;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;

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
                 
                 makeModifierRef(locrg(2, 5, 7), locrg(3, 5, 10), null, "static"));
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
                 
                 makeModifierRef(locrg(2, 5, 9), locrg(3, 5, 7), "final", null));
    }
}
