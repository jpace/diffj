package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Message;
import static org.incava.diffj.function.Throws.*;

public class TestCtorThrows extends ItemsTest {
    protected final static Message[] THROWS_MSGS = new Message[] {
        THROWS_REMOVED,
        null,
        THROWS_ADDED,
    };

    public TestCtorThrows(String name) {
        super(name);
    }

    public void testThrowsAddedNoneToOne() {
        evaluate(new Lines("class Test {",
                           "    Test() {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() throws Exception {}",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_ADDED, "Exception", locrg(2, 5, 13), locrg(3, 19, 27)));
    }

    public void testThrowsAddedOneToTwo() {
        evaluate(new Lines("class Test {",
                           "    Test() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() throws IOException, NullPointerException {}",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", locrg(2, 19, 29), locrg(3, 32, 51)));
    }

    public void testThrowsAddedOneToThree() {
        evaluate(new Lines("class Test {",
                           "    Test() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "}"),

                 makeCodeChangedRef(THROWS_ADDED, "ArrayIndexOutOfBoundsException", locrg(2, 19, 29), locrg(3, 19, 48)),
                 new FileDiffChange(throwsReordMsg("IOException", 0, 1), locrg(2, 19, 29), locrg(3, 51, 61)),
                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", locrg(2, 19, 29), locrg(3, 64, 83)));
    }

    public void testThrowsRemovedOneToNone() {
        evaluate(new Lines("class Test {",
                           "    Test() throws IOException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() {}",
                           "}"),

                 makeCodeChangedRef(THROWS_REMOVED, "IOException", locrg(2, 19, 29), locrg(3, 5, 13)));
    }

    public void testThrowsRemovedTwoToOne() {
        evaluate(new Lines("class Test {",
                           "    Test() throws IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    Test() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", locrg(2, 32, 51), locrg(2, 19, 29)));
    }

    public void testThrowsRemovedThreeToOne() {
        evaluate(new Lines("class Test {",
                           "    Test() throws ArrayIndexOutOfBoundsException, IOException, NullPointerException {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "    Test() throws IOException {}",
                           "",
                           "}"),
                 
                 makeCodeChangedRef(THROWS_REMOVED, "ArrayIndexOutOfBoundsException", locrg(2, 19, 48), locrg(2, 19, 29)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0),  locrg(2, 51, 61), locrg(2, 19, 29)),
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", locrg(2, 64, 83), locrg(2, 19, 29)));
    }

    public void testThrowsReordered() {
        evaluate(new Lines("class Test {",
                           "    Test() throws ArrayIndexOutOfBoundsException, IOException {}",
                           "",
                           "}"),
                 new Lines("class Test {",
                           "    Test() throws IOException, ArrayIndexOutOfBoundsException {}",
                           "",
                           "}"),
                 
                 new FileDiffChange(throwsReordMsg("ArrayIndexOutOfBoundsException", 0, 1), locrg(2, 19, 48), locrg(2, 32, 61)),
                 new FileDiffChange(throwsReordMsg("IOException",                    1, 0), locrg(2, 51, 61), locrg(2, 19, 29)));
    }

    protected String throwsReordMsg(String throwsName, int oldPosition, int newPosition) {
        return THROWS_REORDERED.format(throwsName, oldPosition, newPosition);
    }
}
