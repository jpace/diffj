package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.diffj.params.Parameters;
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", locrg(2, 13, 14), locrg(3, 22, 22)));
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

                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", locrg(2, 13, 22), locrg(3, 24, 32)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "ary", locrg(2, 13, 22), locrg(3, 14, 23)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i",   locrg(2, 13, 22), locrg(3, 36, 44)),
                 makeParamReorderedRef("s", 0, 1, loc(2, 21), loc(3, 33)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", locrg(2, 22, 22), locrg(3, 13, 14)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", locrg(2, 24, 32), locrg(2, 13, 22)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "ary", locrg(2, 14, 23), locrg(2, 13, 22)),
                 makeParamReorderedRef("s", 1, 0, loc(2, 33), loc(2, 21)),
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i",   locrg(2, 36, 44), locrg(2, 13, 22)));
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
                 
                 new FileDiffChange(getMessage(null, null, Parameters.PARAMETER_TYPE_CHANGED, "int", "Integer"), locrg(2, 14, 18), locrg(3, 14, 22)));
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
                 
                 new FileDiffChange(getMessage(null, null, Parameters.PARAMETER_NAME_CHANGED, "i", "x"), locrg(2, 18, 18), locrg(3, 18, 18)));
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
                 
                 makeParamReorderedRef("i", 0, 1, loc(2, 18), loc(3, 28)),
                 makeParamReorderedRef("d", 1, 0, loc(2, 28), loc(3, 21)));
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
                 
                 new FileDiffChange(paramReordRenamedMsg("i", 0, "i2",  1), locrg(2, 18, 18), locrg(3, 30, 31)),
                 new FileDiffChange(paramReordRenamedMsg("d", 1, "dbl", 0), locrg(2, 28, 28), locrg(3, 21, 23)));
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

                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i2", locrg(2, 13, 19), locrg(3, 14, 19)),
                 makeParamReorderedRef("i", 0, 1, loc(2, 18), loc(3, 26)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED,   "obj",     locrg(2, 39, 4, 28), locrg(3, 21, 40)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED,   "string1", locrg(2, 39, 4, 28), locrg(4, 21, 34)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED,   "string2", locrg(2, 39, 4, 28), locrg(4, 37, 50)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED,   "string3", locrg(2, 39, 4, 28), locrg(4, 53, 66)),
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "ctx",     locrg(2, 40, 56), locrg(3, 20, 4, 117)),
                 new FileDiffChange(paramReordMsg("obj1", 1, 4), locrg(3, 9, 27), locrg(4, 69, 91)),
                 new FileDiffChange(paramReordMsg("obj2", 2, 5), locrg(4, 9, 27), locrg(4, 94, 116)));
    }

    protected String paramMsg(String from, String to) {
        return getMessage(Parameters.PARAMETER_REMOVED, Parameters.PARAMETER_ADDED, null, from, to);
    }

    protected FileDiffChange makeParamReorderedRef(String paramName, int oldPosition, int newPosition, Location fromStart, Location toStart) {
        String msg = Parameters.PARAMETER_REORDERED.format(paramName, oldPosition, newPosition);
        return new FileDiffChange(msg, fromStart, loc(fromStart, paramName), toStart, loc(toStart, paramName));
    }

    protected String paramReordMsg(String paramName, int oldPosition, int newPosition) {
        return Parameters.PARAMETER_REORDERED.format(paramName, oldPosition, newPosition);
    }

    protected String paramReordRenamedMsg(String oldName, int oldPosition, String newName, int newPosition) {
        return Parameters.PARAMETER_REORDERED_AND_RENAMED.format(oldName, oldPosition, newPosition, newName);
    }
}
