package org.incava.diffj.field;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.*;
import static org.incava.diffj.field.Variable.*;
import static org.incava.diffj.field.Variables.*;

public class TestFieldVariable extends ItemsTest {
    public TestFieldVariable(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testVariableChanged() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "",
                           "    int j;",
                           "",
                           "}"),

                 makeChangedRef(null, "j", VARIABLE_MSGS, loc(2, 9), loc(2,  9), loc(4, 9), loc(4, 9)),
                 makeChangedRef("i", null, VARIABLE_MSGS, loc(2, 9), loc(2,  9), loc(4, 9), loc(4, 9)));
    }

    public void testVariableWithInitializerChanged() {
        evaluate(new Lines("class Test {",
                           "    int i = 4;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int j = 4;",
                           "}"),
                 
                 makeChangedRef(null, "j", VARIABLE_MSGS, loc(2, 9), loc(2,  9), loc(3, 9), loc(3, 9)),
                 makeChangedRef("i", null, VARIABLE_MSGS, loc(2, 9), loc(2,  9), loc(3, 9), loc(3, 9)));
    }

    public void testVariableTypeChanged() {
        evaluate(new Lines("class Test {",
                           "    Set s;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "",
                           "    HashSet s;",
                           "",
                           "}"),
                 
                 new FileDiffChange(getFromToMessage(VARIABLE_TYPE_CHANGED, "s", "Set", "HashSet"),
                                    loc(2, 5), loc(2, 5, "Set"), 
                                    loc(4, 5), loc(4, 5, "HashSet")));
    }

    public void testMultipleVariables() {
        evaluate(new Lines("class Test {",
                           "    String s, t;",
                           "}"),

                 new Lines("class Test {",
                           "    String s;",
                           "}"),
                 
                 new FileDiffChange(getFromToMessage(VARIABLE_REMOVED, "t"),
                                    loc(2, 15), loc(2, 15, "t"), 
                                    loc(2, 12), loc(2, 12, "t")));
    }
}
