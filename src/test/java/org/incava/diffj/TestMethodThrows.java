package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiffChange;

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

                 makeCodeChangedRef(Messages.THROWS_ADDED, "Exception", loc(2, 5), loc(2, 17), loc(3, 23), loc(3, 31)));
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

                 makeCodeChangedRef(Messages.THROWS_ADDED, "NullPointerException", loc(2, 23), loc(2, 33), loc(3, 36), loc(3, 55)));
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
                 
                 makeCodeChangedRef(Messages.THROWS_ADDED, "ArrayIndexOutOfBoundsException", loc(2, 23), loc(2, 33), loc(3, 23), loc(3, 52)),
                 new FileDiffChange(throwsReordMsg("IOException", 0, 1), loc(2, 23), loc(2, 33), loc(3, 55), loc(3, 65)),
                 makeCodeChangedRef(Messages.THROWS_ADDED, "NullPointerException", loc(2, 23), loc(2, 33), loc(3, 68), loc(3, 87)));
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
                 
                 makeCodeChangedRef(Messages.THROWS_REMOVED, "IOException", loc(2, 23), loc(2, 33), loc(3, 5), loc(3, 17)));
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
                 
                 makeCodeChangedRef(Messages.THROWS_REMOVED, "NullPointerException", loc(2, 36), loc(2, 55), loc(2, 23), loc(2, 33)));
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
                 
                 makeCodeChangedRef(Messages.THROWS_REMOVED, "ArrayIndexOutOfBoundsException", loc(2, 23), loc(2, 52), loc(2, 23), loc(2, 33)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0), loc(2, 55), loc(2, 65), loc(2, 23), loc(2, 33)),
                 makeCodeChangedRef(Messages.THROWS_REMOVED, "NullPointerException", loc(2, 68), loc(2, 87), loc(2, 23), loc(2, 33)));
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
                 
                 new FileDiffChange(throwsReordMsg("ArrayIndexOutOfBoundsException", 0, 1),
                                    loc(2, 23), loc(2, 52), 
                                    loc(2, 36), loc(2, 65)),
                 new FileDiffChange(throwsReordMsg("IOException", 1, 0),
                                    loc(2, 55), loc(2, 65), 
                                    loc(2, 23), loc(2, 33)));
    }

    protected String throwsMsg(String from, String to) {
        return getMessage(Messages.THROWS_REMOVED,
                          Messages.THROWS_ADDED,
                          null, 
                          from, to);
    }

    protected String throwsReordMsg(String throwsName, int oldPosition, int newPosition) {
        return MessageFormat.format(Messages.THROWS_REORDERED, throwsName, oldPosition, newPosition);
    }
}
