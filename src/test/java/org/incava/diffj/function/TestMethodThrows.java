package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import static org.incava.diffj.function.Throws.*;

public class TestMethodThrows extends ItemsTest {
    public TestMethodThrows(String name) {
        super(name);
    }

    public void testThrowsAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws Exception {}",
                           "}"),

                 makeCodeChangedRef(THROWS_ADDED, "Exception", locrg(2, 5, 17), locrg(3, 23, 31)));
    }

    public void testThrowsAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws IOException, NullPointerException {}",
                           "}"),

                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", locrg(2, 23, 33), locrg(3, 36, 55)));
    }

    public void testThrowsAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_ADDED, "ArrayIndexOutOfBoundsException", locrg(2, 23, 33), locrg(3, 23, 52)),
                 new FileDiffChange(locrg(2, 23, 33), locrg(3, 55, 65), THROWS_REORDERED, "IOException", 0, 1),
                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", locrg(2, 23, 33), locrg(3, 68, 87)));
    }

    public void testThrowsRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    void foo() {}",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_REMOVED, "IOException", locrg(2, 23, 33), locrg(3, 5, 17)));
    }

    public void testThrowsRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", locrg(2, 36, 55), locrg(2, 23, 33)));
    }

    public void testThrowsRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_REMOVED, "ArrayIndexOutOfBoundsException", locrg(2, 23, 52), locrg(2, 23, 33)),
                 new FileDiffChange(locrg(2, 55, 65), locrg(2, 23, 33), THROWS_REORDERED, "IOException", 1, 0),
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", locrg(2, 68, 87), locrg(2, 23, 33)));
    }

    public void testThrowsReordered() {
        evaluate(new Lines("class Test {",
                           "    void foo() throws ArrayIndexOutOfBoundsException, IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    void foo() throws IOException, ArrayIndexOutOfBoundsException {}",
                           "",
                           "}"),
                 
                 new FileDiffChange(locrg(2, 23, 52), locrg(2, 36, 65), THROWS_REORDERED, "ArrayIndexOutOfBoundsException", 0, 1),
                 new FileDiffChange(locrg(2, 55, 65), locrg(2, 23, 33), THROWS_REORDERED, "IOException", 1, 0));
    }
}
