package org.incava.diffj;

import org.incava.analysis.FileDiffChange;
import static org.incava.diffj.code.Code.*;
import static org.incava.diffj.field.Variable.*;

public class TestFieldInitializer extends ItemsTest {
    public TestFieldInitializer(String name) {
        super(name);
        tr.Ace.setVerbose(true);
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
                 
                 new FileDiffChange(INITIALIZER_ADDED, loc(2, 9), loc(2, 9), loc(3, 13), loc(3, 13)));
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
                 
                 new FileDiffChange(INITIALIZER_REMOVED, loc(2, 13), loc(2, 13), loc(3, 9), loc(3, 9)));
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
                 
                 makeCodeChangedRef(CODE_CHANGED, "i", loc(2, 13), loc(2, 13), loc(3, 13), loc(3, 13)));
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
                 
                 makeCodeAddedRef(CODE_ADDED, "i", loc(2, 14), loc(2, 14), loc(3, 15), loc(3, 17)));
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
                 
                 makeCodeDeletedRef(CODE_REMOVED, "i", loc(2, 15), loc(2, 17), loc(3, 14), loc(3, 14)));
    }
}
