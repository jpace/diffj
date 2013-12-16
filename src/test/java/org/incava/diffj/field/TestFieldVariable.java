package org.incava.diffj.field;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Message;
import static org.incava.diffj.field.Variable.*;
import static org.incava.diffj.field.Variables.*;

public class TestFieldVariable extends ItemsTest {
    protected final static Message[] VARIABLE_MSGS = new Message[] {
        Variables.VARIABLE_REMOVED,
        Variables.VARIABLE_CHANGED, 
        Variables.VARIABLE_ADDED,
    };

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

                 makeChangedRef(locrg(2, 9, 9), locrg(4, 9, 9), VARIABLE_MSGS, null, "j"),
                 makeChangedRef(locrg(2, 9, 9), locrg(4, 9, 9), VARIABLE_MSGS, "i", null));
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
                 
                 makeChangedRef(locrg(2, 9, 9), locrg(3, 9, 9), VARIABLE_MSGS, null, "j"),
                 makeChangedRef(locrg(2, 9, 9), locrg(3, 9, 9), VARIABLE_MSGS, "i", null));
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
                 
                 new FileDiffChange(VARIABLE_TYPE_CHANGED.format("s", "Set", "HashSet"),
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
                 
                 new FileDiffChange(VARIABLE_REMOVED.format("t"),
                                    loc(2, 15), loc(2, 15, "t"), 
                                    loc(2, 12), loc(2, 12, "t")));
    }
}
