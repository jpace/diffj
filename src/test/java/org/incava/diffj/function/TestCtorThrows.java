package org.incava.diffj.function;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.*;
import static org.incava.diffj.function.Throws.*;

public class TestCtorThrows extends ItemsTest {
    protected final static String[] THROWS_MSGS = new String[] {
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
                 
                 makeCodeChangedRef(THROWS_ADDED, "Exception", loc(2, 5), loc(2, 13), loc(3, 19), loc(3, 27)));
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
                 
                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", loc(2, 19), loc(2, 29), loc(3, 32), loc(3, 51)));
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

                 makeCodeChangedRef(THROWS_ADDED, "ArrayIndexOutOfBoundsException", loc(2, 19), loc(2, 29), loc(3, 19), loc(3, 48)),
                 new FileDiffChange(throwsReordMsg("IOException", 0, 1), loc(2, 19), loc(2, 29), loc(3, 51), loc(3, 61)),
                 makeCodeChangedRef(THROWS_ADDED, "NullPointerException", loc(2, 19), loc(2, 29), loc(3, 64), loc(3, 83)));
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

                 makeCodeChangedRef(THROWS_REMOVED, "IOException", loc(2, 19), loc(2, 29), loc(3, 5), loc(3, 13)));
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
                 
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", loc(2, 32), loc(2, 51), loc(2, 19), loc(2, 29)));
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
                 
                 makeCodeChangedRef(THROWS_REMOVED, "ArrayIndexOutOfBoundsException", loc(2, 19), loc(2, 48), loc(2, 19), loc(2, 29)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0),  loc(2, 51), loc(2, 61), loc(2, 19), loc(2, 29)),
                 makeCodeChangedRef(THROWS_REMOVED, "NullPointerException", loc(2, 64), loc(2, 83), loc(2, 19), loc(2, 29)));
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
                 
                 new FileDiffChange(throwsReordMsg("ArrayIndexOutOfBoundsException", 0, 1), loc(2, 19), loc(2, 48), loc(2, 32), loc(2, 61)),
                 new FileDiffChange(throwsReordMsg("IOException",                    1, 0), loc(2, 51), loc(2, 61), loc(2, 19), loc(2, 29)));
    }

    protected String throwsReordMsg(String throwsName, int oldPosition, int newPosition) {
        return MessageFormat.format(THROWS_REORDERED, throwsName, oldPosition, newPosition);
    }
}
