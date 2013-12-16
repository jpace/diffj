package org.incava.diffj.field;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import static org.incava.diffj.code.Code.*;
import static org.incava.diffj.field.Variable.*;

public class TestFieldInitializer extends ItemsTest {
    public TestFieldInitializer(String name) {
        super(name);
    }

    public void testInitializerAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i = 4;",
                           "}"),
                 
                 new FileDiffChange(INITIALIZER_ADDED.format(), locrg(2, 9, 9), locrg(3, 13, 13)));
    }

    public void testInitializerRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i = 4;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 new FileDiffChange(INITIALIZER_REMOVED.format(), locrg(2, 13, 13), locrg(3, 9, 9)));
    }

    public void testInitializerCodeChanged() {
        evaluate(new Lines("class Test {",
                           "    int i = 4;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i = 5;",
                           "}"),
                 
                 makeCodeChangedRef(CODE_CHANGED, "i", locrg(2, 13, 13), locrg(3, 13, 13)));
    }

    public void testInitializerCodeAdded() {
        evaluate(new Lines("class Test {",
                           "    int i = 4;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i = 4 * 5;",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "i", locrg(2, 14, 14), locrg(3, 15, 17)));
    }

    public void testInitializerCodeRemoved() {
        evaluate(new Lines("class Test {",
                           "    int i = 4 * 5;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i = 4;",
                           "}"),
                 
                 makeCodeDeletedRef(CODE_REMOVED, "i", locrg(2, 15, 17), locrg(3, 14, 14)));
    }
}
