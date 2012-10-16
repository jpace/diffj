package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.*;
import org.incava.diffj.params.Parameters;
import org.incava.ijdk.text.Location;

public class TestCtorParameters extends ItemsTest {
    protected final static String[] PARAMETER_MSGS = new String[] {
        Parameters.PARAMETER_REMOVED,
        null,
        Parameters.PARAMETER_ADDED,
    };

    public TestCtorParameters(String name) {
        super(name);
    }

    public void testParameterAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    Test() {}",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    Test(Integer i) {}",
                           "}"),

                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", loc(2, 9), loc(2, 10), loc(3, 18), loc(3, 18)));
    }

    public void testParameterAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    Test(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(String s, Integer i) {}",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", loc(2, 9), loc(2, 18), loc(3, 20), loc(3, 28)));
    }

    public void testParameterAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    Test(String s) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(List[] ary, String s, Integer i) {}",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "ary", loc(2,  9), loc(2, 18), loc(3, 10), loc(3, 19)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i",   loc(2,  9), loc(2, 18), loc(3, 32), loc(3, 40)),
                 new FileDiffChange(paramReordMsg("s", 0, 1), loc(2, 17), loc(2, 17), loc(3, 29), loc(3, 29)));
    }

    public void testParameterRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    Test(Integer i[][][][]) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() {}",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", loc(2, 18), loc(2, 18), loc(3, 9), loc(3, 10)));
    }

    public void testParameterRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    Test(String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    Test(String s) {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", loc(2, 20), loc(2, 28), loc(2, 9), loc(2, 18)));
    }

    public void testParameterRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    Test(List[] ary, String s, Integer i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    Test(String s) {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "ary", loc(2, 10), loc(2, 19), loc(2,  9), loc(2, 18)),
                 new FileDiffChange(paramReordMsg("s", 1, 0), loc(2, 29), loc(2, 29), loc(2, 17), loc(2, 17)),
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i",   loc(2, 32), loc(2, 40), loc(2,  9), loc(2, 18)));
    }

    public void testParameterChangedType() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(Integer i) {}",
                           "}"),
                 
                 new FileDiffChange(getMessage(null, null, Parameters.PARAMETER_TYPE_CHANGED, "int", "Integer"), 
                                          loc(2, 10), loc(2, 14), loc(3, 10), loc(3, 18)));
    }

    public void testParameterChangedName() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(int x) {}",
                           "}"),
                 
                 makeChangedRef(Parameters.PARAMETER_NAME_CHANGED, "i", "x", loc(2, 14), loc(3, 14)));
    }

    public void testParameterReordered() {
        evaluate(new Lines("class Test {",
                           "    Test(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(double d, int i) {}",
                           "}"),
                 
                 new FileDiffChange(paramReordMsg("i", 0, 1), loc(2, 14), loc(2, 14), loc(3, 24), loc(3, 24)),
                 new FileDiffChange(paramReordMsg("d", 1, 0), loc(2, 24), loc(2, 24), loc(3, 17), loc(3, 17)));
    }

    public void testParameterReorderedAndRenamed() {
        evaluate(new Lines("class Test {",
                           "    Test(int i, double d) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(double dbl, int i2) {}",
                           "}"),
                 
                 new FileDiffChange(paramReordRenamedMsg("i", 0, "i2",  1), loc(2, 14), loc(2, 14), loc(3, 26), loc(3, 27)),
                 new FileDiffChange(paramReordRenamedMsg("d", 1, "dbl", 0), loc(2, 24), loc(2, 24), loc(3, 17), loc(3, 19)));
    }

    public void testParameterOneAddedOneReordered() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(int i2, int i) {}",
                           "}"),
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i2", loc(2,  9), loc(2, 15), loc(3, 10), loc(3, 15)),
                 new FileDiffChange(paramReordMsg("i", 0, 1), loc(2, 14), loc(2, 14), loc(3, 22), loc(3, 22)));
    }

    protected String paramReordMsg(String paramName, int oldPosition, int newPosition) {
        return MessageFormat.format(Parameters.PARAMETER_REORDERED, paramName, Integer.valueOf(oldPosition), Integer.valueOf(newPosition));
    }

    protected String paramReordRenamedMsg(String oldName, int oldPosition, String newName, int newPosition) {
        return MessageFormat.format(Parameters.PARAMETER_REORDERED_AND_RENAMED, oldName, Integer.valueOf(oldPosition), Integer.valueOf(newPosition), newName);
    }

    protected FileDiff makeChangedRef(String msg, 
                                      String fromStr, String toStr,
                                      Location fromStart, Location toStart) {
        return new FileDiffChange(getMessage(null, null, msg, fromStr, toStr), fromStart, loc(fromStart, fromStr), toStart, loc(toStart, toStr));
    }
}
