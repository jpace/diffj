package org.incava.diffj;

import org.incava.analysis.FileDiffChange;

public class TestFields extends ItemTest {
    public TestFields(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testAccessAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    private int i;",
                           "}"),
                 
                 makeAccessRef(null, "private", loc(2, 5), loc(2, 7), loc(3, 5), loc(3, 11)));
    }

    public void testAccessRemoved() {
        evaluate(new Lines("class Test {",
                           "    public int i;",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    int i;",
                           "}"),
                 
                 makeAccessRef("public", null, loc(2, 5), loc(2, 10), loc(3, 5), loc(3, 7)));
    }

    public void testAccessChanged() {
        evaluate(new Lines("class Test {",
                           "    private int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    public int i;",
                           "}"),
                 
                 makeAccessRef("private", "public", loc(2, 5), loc(3, 5)));
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

    public void testInitializerAdded() {
        evaluate(new Lines("class Test {",
                           "    int i;",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    int i = 4;",
                           "}"),
                 
                 new FileDiffChange(Messages.INITIALIZER_ADDED, loc(2, 9), loc(2, 9), loc(3, 13), loc(3, 13)));
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
                 
                 new FileDiffChange(Messages.INITIALIZER_REMOVED, loc(2, 13), loc(2, 13), loc(3, 9), loc(3, 9)));
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
                 
                 makeCodeChangedRef(Messages.CODE_CHANGED, "i", loc(2, 13), loc(2, 13), loc(3, 13), loc(3, 13)));
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
                 
                 makeCodeAddedRef(Messages.CODE_ADDED, "i", loc(2, 14), loc(2, 14), loc(3, 15), loc(3, 17)));
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
                 
                 makeCodeDeletedRef(Messages.CODE_REMOVED, "i", loc(2, 15), loc(2, 17), loc(3, 14), loc(3, 14)));
    }

    // public static class TestFieldDiffVariableAddedRemoved extends ItemTest {
        
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
    //                  makeCodeChangedRef(Messages.VARIABLE_REMOVED, "v",  loc(5, 33), loc(5, 33), loc(6, 25), loc(6, 25)),
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
                 
                 makeVariableRef("i", "j", loc(2, 9), loc(2,  9), loc(4, 9), loc(4, 9)));
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
                 
                 makeVariableRef("i", "j", loc(2, 9), loc(2, 9), loc(3, 9), loc(3, 9)));
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
                 
                 new FileDiffChange(getFromToMessage(Messages.VARIABLE_TYPE_CHANGED, "s", "Set", "HashSet"),
                                          loc(2, 5), loc(2, 5, "Set"), 
                                          loc(4, 5), loc(4, 5, "HashSet")));
    }
}
