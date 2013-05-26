package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.*;
import org.incava.diffj.params.Parameters;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.Message;

public class TestCtorParameters extends ItemsTest {
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

                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", locrg(2, 9, 10), locrg(3, 18, 18)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i", locrg(2, 9, 18), locrg(3, 20, 28)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "ary", locrg(2, 9, 18), locrg(3, 10, 19)),
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i",   locrg(2, 9, 18), locrg(3, 32, 40)),
                 new FileDiffChange(paramReordMsg("s", 0, 1), locrg(2, 17, 17), locrg(3, 29, 29)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", locrg(2, 18, 18), locrg(3, 9, 10)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i", locrg(2, 20, 28), locrg(2, 9, 18)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "ary", locrg(2, 10, 19), locrg(2, 9, 18)),
                 new FileDiffChange(paramReordMsg("s", 1, 0), locrg(2, 29, 29), locrg(2, 17, 17)),
                 makeCodeChangedRef(Parameters.PARAMETER_REMOVED, "i",   locrg(2, 32, 40), locrg(2, 9, 18)));
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
                                    locrg(2, 10, 14), locrg(3, 10, 18)));
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
                 
                 new FileDiffChange(paramReordMsg("i", 0, 1), locrg(2, 14, 14), locrg(3, 24, 24)),
                 new FileDiffChange(paramReordMsg("d", 1, 0), locrg(2, 24, 24), locrg(3, 17, 17)));
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
                 
                 new FileDiffChange(paramReordRenamedMsg("i", 0, "i2",  1), locrg(2, 14, 14), locrg(3, 26, 27)),
                 new FileDiffChange(paramReordRenamedMsg("d", 1, "dbl", 0), locrg(2, 24, 24), locrg(3, 17, 19)));
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
                 
                 makeCodeChangedRef(Parameters.PARAMETER_ADDED, "i2", locrg(2, 9, 15), locrg(3, 10, 15)),
                 new FileDiffChange(paramReordMsg("i", 0, 1), locrg(2, 14, 14), locrg(3, 22, 22)));
    }

    protected String paramReordMsg(String paramName, int oldPosition, int newPosition) {
        return Parameters.PARAMETER_REORDERED.format(paramName, oldPosition, newPosition);
    }

    protected String paramReordRenamedMsg(String oldName, int oldPosition, String newName, int newPosition) {
        return Parameters.PARAMETER_REORDERED_AND_RENAMED.format(oldName, oldPosition, newPosition, newName);
    }

    protected FileDiff makeChangedRef(Message msg, 
                                      String fromStr, String toStr,
                                      Location fromStart, Location toStart) {
        return new FileDiffChange(getMessage(null, null, msg, fromStr, toStr), fromStart, loc(fromStart, fromStr), toStart, loc(toStart, toStr));
    }
}
