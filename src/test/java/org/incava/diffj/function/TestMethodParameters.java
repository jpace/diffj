package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.params.Parameters;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Location;

public class TestMethodParameters extends ItemsTest {
    public TestMethodParameters(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testParameterAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(Integer i) {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 13, 14), locrg(3, 22, 22), Parameters.PARAMETER_ADDED, "i"));
    }

    public void testParameterAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(String s, Integer i) {}",
                           "}"),

                 new FileDiffChange(locrg(2, 13, 22), locrg(3, 24, 32), Parameters.PARAMETER_ADDED, "i"));
    }

    public void testParameterAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(List[] ary, String s, Integer i) {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 13, 22), locrg(3, 14, 23), Parameters.PARAMETER_ADDED, "ary"),
                 new FileDiffChange(locrg(2, 13, 22), locrg(3, 36, 44), Parameters.PARAMETER_ADDED, "i"),
                 makeParamReorderedRef(loc(2, 21), loc(3, 33), "s", 0, 1));
    }

    public void testParameterRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    void foo(Integer i[][][][]) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 22, 22), locrg(3, 13, 14), Parameters.PARAMETER_REMOVED, "i"));
    }

    public void testParameterRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo(String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 24, 32), locrg(2, 13, 22), Parameters.PARAMETER_REMOVED, "i"));
    }

    public void testParameterRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo(List[] ary, String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo(String s) {}",
                           "",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 14, 23), locrg(2, 13, 22), Parameters.PARAMETER_REMOVED, "ary"),
                 makeParamReorderedRef(loc(2, 33), loc(2, 21), "s", 1, 0),
                 new FileDiffChange(locrg(2, 36, 44), locrg(2, 13, 22), Parameters.PARAMETER_REMOVED, "i"));
    }

    public void testParameterChangedType() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(Integer i) {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 14, 18), locrg(3, 14, 22), Parameters.PARAMETER_TYPE_CHANGED, "int", "Integer"));
    }

    public void testParameterChangedName() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(int x) {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 18, 18), locrg(3, 18, 18), Parameters.PARAMETER_NAME_CHANGED, "i", "x"));
    }

    public void testParameterReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(double d, int i) {}",
                           "}"),
                 
                 makeParamReorderedRef(loc(2, 18), loc(3, 28), "i", 0, 1),
                 makeParamReorderedRef(loc(2, 28), loc(3, 21), "d", 1, 0));
    }

    public void testParameterReorderedAndRenamed() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(double dbl, int i2) {}",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 18, 18), locrg(3, 30, 31), Parameters.PARAMETER_REORDERED_AND_RENAMED, "i", 0, 1, "i2"),
                 new FileDiffChange(locrg(2, 28, 28), locrg(3, 21, 23), Parameters.PARAMETER_REORDERED_AND_RENAMED, "d", 1, 0, "dbl"));
    }

    public void testParameterOneAddedOneReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo(int i2, int i) {}",
                           "}"),

                 new FileDiffChange(locrg(2, 13, 19), locrg(3, 14, 19), Parameters.PARAMETER_ADDED, "i2"),
                 makeParamReorderedRef(loc(2, 18), loc(3, 26), "i", 0, 1));
    }

    public void testParameterReorderedByName() {
        evaluate(new Lines("public abstract class AbstractClass {",
                           "    public abstract String javaMethod1(final Context ctx,",
                           "        final Object[] obj1,",
                           "        final Object[] obj2);",
                           "}"),

                 new Lines("public abstract class AbstractClass {",
                           "    public abstract String",
                           "        javaMethod1(java.lang.Object obj, ",
                           "                    String string1, String string2, String string3, java.lang.Object[] obj1, java.lang.Object[] obj2);",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 39, 4, 28), locrg(3, 21, 40), Parameters.PARAMETER_ADDED,   "obj"),
                 new FileDiffChange(locrg(2, 39, 4, 28), locrg(4, 21, 34), Parameters.PARAMETER_ADDED, "string1"),
                 new FileDiffChange(locrg(2, 39, 4, 28), locrg(4, 37, 50), Parameters.PARAMETER_ADDED, "string2"),
                 new FileDiffChange(locrg(2, 39, 4, 28), locrg(4, 53, 66), Parameters.PARAMETER_ADDED,   "string3"),
                 new FileDiffChange(locrg(2, 40, 56), locrg(3, 20, 4, 117), Parameters.PARAMETER_REMOVED, "ctx"),
                 new FileDiffChange(locrg(3, 9, 27), locrg(4, 69, 91), Parameters.PARAMETER_REORDERED, "obj1", 1, 4),
                 new FileDiffChange(locrg(4, 9, 27), locrg(4, 94, 116), Parameters.PARAMETER_REORDERED, "obj2", 2, 5));
    }

    protected FileDiffChange makeParamReorderedRef(Location fromStart, Location toStart, String paramName, int oldPosition, int newPosition) {
        String msg = Parameters.PARAMETER_REORDERED.format(paramName, oldPosition, newPosition);
        return new FileDiffChange(msg, fromStart, loc(fromStart, paramName), toStart, loc(toStart, paramName));
    }
}
