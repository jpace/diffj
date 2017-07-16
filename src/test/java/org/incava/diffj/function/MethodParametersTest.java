package org.incava.diffj.function;

import org.incava.diffj.util.Lines;

public class MethodParametersTest extends FunctionTestCase {
    public MethodParametersTest(String name) {
        super(name);
    }

    private Lines testCode(String params) {
        return lines("class Test {",
                     "    void abc(" + params + ") {}",
                     "}");
    }
    
    public void testParameterAddedNoneToOne() {
        evaluate(testCode(""),
                 testCode("Integer i"),
                 paramAdded("i", locrg(2, 13, 14), locrg(2, 22, 22)));
    }

    public void testParameterAddedOneToTwo() {
        evaluate(testCode("String s"),
                 testCode("String s, Integer i"),
                 paramAdded("i", locrg(2, 13, 22), locrg(2, 24, 32)));
    }

    public void testParameterAddedOneToThree() {
        evaluate(testCode("String s"),
                 testCode("List[] ary, String s, Integer i"),
                 
                 paramAdded("ary", locrg(2, 13, 22), locrg(2, 14, 23)),
                 paramAdded("i",   locrg(2, 13, 22), locrg(2, 36, 44)),
                 paramReordered("s", 0, 1, locrg(2, 21, 21), locrg(2, 33, 33)));
    }

    public void testParameterRemovedOneToNone() {
        evaluate(testCode("Integer i[][][][]"),
                 testCode(""),
                 
                 paramRemoved("i", locrg(2, 22, 22), locrg(2, 13, 14)));
    }

    public void testParameterRemovedTwoToOne() {
        evaluate(testCode("String s, Integer i"),
                 testCode("String s"),
                 
                 paramRemoved("i", locrg(2, 24, 32), locrg(2, 13, 22)));
    }

    public void testParameterRemovedThreeToOne() {
        evaluate(testCode("List[] ary, String s, Integer i"),
                 testCode("String s"),
                 
                 paramRemoved("ary",       locrg(2, 14, 23), locrg(2, 13, 22)),
                 paramReordered("s", 1, 0, locrg(2, 33, 33), locrg(2, 21, 21)),
                 paramRemoved("i",         locrg(2, 36, 44), locrg(2, 13, 22)));
    }

    public void testParameterChangedType() {
        evaluate(testCode("int i"),
                 testCode("Integer i"),
                 
                 paramTypeChanged("int", "Integer", locrg(2, 14, 18), locrg(2, 14, 22)));
    }

    public void testParameterChangedName() {
        evaluate(testCode("int i"),
                 testCode("int x"),
                 
                 paramNameChanged("i", "x", locrg(2, 18, 18), locrg(2, 18, 18)));
    }

    public void testParameterReordered() {
        evaluate(testCode("int i, double d"),
                 testCode("double d, int i"),
                 
                 paramReordered("i", 0, 1, locrg(2, 18, 18), locrg(2, 28, 28)),
                 paramReordered("d", 1, 0, locrg(2, 28, 28), locrg(2, 21, 21)));
    }

    public void testParameterReorderedAndRenamed() {
        evaluate(testCode("int i, double d"),
                 testCode("double dbl, int i2"),

                 paramReorderedAndRenamed("i", 0, 1, "i2",  locrg(2, 18, 18), locrg(2, 30, 31)),
                 paramReorderedAndRenamed("d", 1, 0, "dbl", locrg(2, 28, 28), locrg(2, 21, 23)));
    }

    public void testParameterOneAddedOneReordered() {
        evaluate(testCode("int i"),
                 testCode("int i2, int i"),

                 paramAdded("i2",          locrg(2, 13, 19), locrg(2, 14, 19)),
                 paramReordered("i", 0, 1, locrg(2, 18, 18), locrg(2, 26, 26)));
    }

    public void testParameterReorderedByName() {
        evaluate(lines("public abstract class AbstractClass {",
                       "    public abstract String javaMethod1(final Context ctx,",
                       "        final Object[] obj1,",
                       "        final Object[] obj2);",
                       "}"),

                 lines("public abstract class AbstractClass {",
                       "    public abstract String",
                       "        javaMethod1(java.lang.Object obj, ",
                       "                    String string1, String string2, String string3, java.lang.Object[] obj1, java.lang.Object[] obj2);",
                       "}"),
                 
                 paramAdded("obj",            locrg(2, 39, 4, 28), locrg(3, 21, 40)),
                 paramAdded("string1",        locrg(2, 39, 4, 28), locrg(4, 21, 34)),
                 paramAdded("string2",        locrg(2, 39, 4, 28), locrg(4, 37, 50)),
                 paramAdded("string3",        locrg(2, 39, 4, 28), locrg(4, 53, 66)),
                 paramRemoved("ctx",          locrg(2, 40, 56),    locrg(3, 20, 4, 117)),
                 paramReordered("obj1", 1, 4, locrg(3, 9, 27),     locrg(4, 69, 91)),
                 paramReordered("obj2", 2, 5, locrg(4, 9, 27),     locrg(4, 94, 116)));
    }
}
