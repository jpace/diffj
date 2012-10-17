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

    // public static class TestFieldDiffVariableAddedRemoved extends ItemsTest {
        
    //     public TestFieldDiffVariableAddedRemoved(String name) {
    //         super(name);
    //     }
        
    //     public void testVariableAddedRemoved() {
    //         evaluate(new Lines("public class Collections {",
    //                  "",
    //                  "    private static class SingletonMap",
    //                  "                                      implements Serializable {",
    //                  "        private final Object k, v;",
    //                  "    }",
    //                  "}"),
    //                  "public class Collections {",
    //                  "",
    //                  "    private static class SingletonMap<K,V>",
    //                  "	  implements Serializable {",
    //                  "",
    //                  "        private final K k;",
    //                  "        private final V v;",
    //                  "    }",
    //                  "}"),
    // wrong!
    // these should be:

    //                  // variable type changed for k from Object to K
    //                  // variable type changed for v from Object to V

    //                  makeFieldRef(null, "v",  loc(3, 20), loc(6,  5), loc(7, 23), loc(7, 26)),
    //                  makeCodeChangedRef(VARIABLE_REMOVED, "v",  loc(5, 33), loc(5, 33), loc(6, 25), loc(6, 25)),
    //                  Java.SOURCE_1_5);
    //     }
    // }

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
