package org.incava.diffj.function;

public class MethodParametersTest extends FunctionTestCase {
    public MethodParametersTest(String name) {
        super(name);
    }

    public void testParameterAddedNoneToOne() {
        evaluate(lines("class Test {",
                       "    void foo() {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(Integer i) {}",
                       "}"),

                 paramAdded("i", locrg(2, 13, 14), locrg(3, 22, 22)));
    }

    public void testParameterAddedOneToTwo() {
        evaluate(lines("class Test {",
                       "    void foo(String s) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(String s, Integer i) {}",
                       "}"),

                 paramAdded("i", locrg(2, 13, 22), locrg(3, 24, 32)));
    }

    public void testParameterAddedOneToThree() {
        evaluate(lines("class Test {",
                       "    void foo(String s) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(List[] ary, String s, Integer i) {}",
                       "}"),
                 
                 paramAdded("ary", locrg(2, 13, 22), locrg(3, 14, 23)),
                 paramAdded("i",   locrg(2, 13, 22), locrg(3, 36, 44)),
                 paramReordered("s", 0, 1, locrg(2, 21, 21), locrg(3, 33, 33)));
    }

    public void testParameterRemovedOneToNone() {
        evaluate(lines("class Test {",
                       "    void foo(Integer i[][][][]) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo() {}",
                       "}"),
                 
                 paramRemoved("i", locrg(2, 22, 22), locrg(3, 13, 14)));
    }

    public void testParameterRemovedTwoToOne() {
        evaluate(lines("class Test {",
                       "    void foo(String s, Integer i) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "    void foo(String s) {}",
                       "",
                       "}"),
                 
                 paramRemoved("i", locrg(2, 24, 32), locrg(2, 13, 22)));
    }

    public void testParameterRemovedThreeToOne() {
        evaluate(lines("class Test {",
                       "    void foo(List[] ary, String s, Integer i) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "    void foo(String s) {}",
                       "",
                       "}"),
                 
                 paramRemoved("ary", locrg(2, 14, 23), locrg(2, 13, 22)),
                 paramReordered("s", 1, 0, locrg(2, 33, 33), locrg(2, 21, 21)),
                 paramRemoved("i", locrg(2, 36, 44), locrg(2, 13, 22)));
    }

    public void testParameterChangedType() {
        evaluate(lines("class Test {",
                       "    void foo(int i) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(Integer i) {}",
                       "}"),
                 
                 paramTypeChanged("int", "Integer", locrg(2, 14, 18), locrg(3, 14, 22)));
    }

    public void testParameterChangedName() {
        evaluate(lines("class Test {",
                       "    void foo(int i) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(int x) {}",
                       "}"),
                 
                 paramNameChanged("i", "x", locrg(2, 18, 18), locrg(3, 18, 18)));
    }

    public void testParameterReordered() {
        evaluate(lines("class Test {",
                       "    void foo(int i, double d) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(double d, int i) {}",
                       "}"),
                 
                 paramReordered("i", 0, 1, locrg(2, 18, 18), locrg(3, 28, 28)),
                 paramReordered("d", 1, 0, locrg(2, 28, 28), locrg(3, 21, 21)));
    }

    public void testParameterReorderedAndRenamed() {
        evaluate(lines("class Test {",
                       "    void foo(int i, double d) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(double dbl, int i2) {}",
                       "}"),

                 paramReorderedAndRenamed("i", 0, 1, "i2", locrg(2, 18, 18), locrg(3, 30, 31)),
                 paramReorderedAndRenamed("d", 1, 0, "dbl", locrg(2, 28, 28), locrg(3, 21, 23)));
    }

    public void testParameterOneAddedOneReordered() {
        evaluate(lines("class Test {",
                       "    void foo(int i) {}",
                       "",
                       "}"),

                 lines("class Test {",
                       "",
                       "    void foo(int i2, int i) {}",
                       "}"),

                 paramAdded("i2", locrg(2, 13, 19), locrg(3, 14, 19)),
                 paramReordered("i", 0, 1, locrg(2, 18, 18), locrg(3, 26, 26)));
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
                 
                 paramAdded("obj", locrg(2, 39, 4, 28), locrg(3, 21, 40)),
                 paramAdded("string1", locrg(2, 39, 4, 28), locrg(4, 21, 34)),
                 paramAdded("string2", locrg(2, 39, 4, 28), locrg(4, 37, 50)),
                 paramAdded("string3", locrg(2, 39, 4, 28), locrg(4, 53, 66)),
                 paramRemoved("ctx", locrg(2, 40, 56), locrg(3, 20, 4, 117)),
                 paramReordered("obj1", 1, 4, locrg(3, 9, 27), locrg(4, 69, 91)),
                 paramReordered("obj2", 2, 5, locrg(4, 9, 27), locrg(4, 94, 116)));
    }
}
