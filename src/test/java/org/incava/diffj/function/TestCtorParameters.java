package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.Message;
import static org.incava.diffj.params.Parameters.*;

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

                 makeCodeChangedRef(PARAMETER_ADDED, "i", locrg(2, 9, 10), locrg(3, 18, 18)));
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
                 
                 makeCodeChangedRef(PARAMETER_ADDED, "i", locrg(2, 9, 18), locrg(3, 20, 28)));
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
                 
                 makeCodeChangedRef(PARAMETER_ADDED, "ary", locrg(2, 9, 18), locrg(3, 10, 19)),
                 makeCodeChangedRef(PARAMETER_ADDED, "i",   locrg(2, 9, 18), locrg(3, 32, 40)),
                 new FileDiffChange(PARAMETER_REORDERED.format("s", 0, 1), locrg(2, 17, 17), locrg(3, 29, 29)));
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
                 
                 makeCodeChangedRef(PARAMETER_REMOVED, "i", locrg(2, 18, 18), locrg(3, 9, 10)));
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
                 
                 makeCodeChangedRef(PARAMETER_REMOVED, "i", locrg(2, 20, 28), locrg(2, 9, 18)));
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
                 
                 makeCodeChangedRef(PARAMETER_REMOVED, "ary", locrg(2, 10, 19), locrg(2, 9, 18)),
                 new FileDiffChange(PARAMETER_REORDERED.format("s", 1, 0), locrg(2, 29, 29), locrg(2, 17, 17)),
                 makeCodeChangedRef(PARAMETER_REMOVED, "i",   locrg(2, 32, 40), locrg(2, 9, 18)));
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
                 
                 new FileDiffChange(PARAMETER_TYPE_CHANGED.format("int", "Integer"), locrg(2, 10, 14), locrg(3, 10, 18)));
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

                 new FileDiffChange(PARAMETER_NAME_CHANGED.format("i", "x"), locrg(2, 14, 14), locrg(3, 14, 14)));
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
                 
                 new FileDiffChange(PARAMETER_REORDERED.format("i", 0, 1), locrg(2, 14, 14), locrg(3, 24, 24)),
                 new FileDiffChange(PARAMETER_REORDERED.format("d", 1, 0), locrg(2, 24, 24), locrg(3, 17, 17)));
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
                 
                 new FileDiffChange(PARAMETER_REORDERED_AND_RENAMED.format("i", 0, 1, "i2"), locrg(2, 14, 14), locrg(3, 26, 27)),
                 new FileDiffChange(PARAMETER_REORDERED_AND_RENAMED.format("d", 1, 0, "dbl"), locrg(2, 24, 24), locrg(3, 17, 19)));
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
                 
                 makeCodeChangedRef(PARAMETER_ADDED, "i2", locrg(2, 9, 15), locrg(3, 10, 15)),
                 new FileDiffChange(PARAMETER_REORDERED.format("i", 0, 1), locrg(2, 14, 14), locrg(3, 22, 22)));
    }
}
