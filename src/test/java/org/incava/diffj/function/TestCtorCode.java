package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import static org.incava.diffj.code.Code.*;

public class TestCtorCode extends ItemsTest {
    public TestCtorCode(String name) {
        super(name);
        tr.Ace.setVerbose(true);
    }

    public void testCodeNotChanged() {
        evaluate(new Lines("class Test {",
                           "    Test() { i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        i = -1;",
                           "    }",
                           "",
                           "}"),
                 
                 NO_CHANGES);
    }

    public void testCodeChanged() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int i = -2;",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(CODE_CHANGED, "Test()", locrg(2, 23, 23), locrg(4, 18, 18)));
    }

    public void testCodeChangedInserted() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int i = -2;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(2, 19, 19), locrg(4, 15, 18)));
    }
    
    public void testCodeInsertedSameLine() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i = -1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int j = 0;",
                           "        int i = -1;",
                           "    }",
                           "}"),
                 
                 // the new implementation:
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(2, 14, 16), locrg(4, 9, 18)));

                 // the old one:
                 // makeCodeAddedRef(CODE_ADDED, "Test()", locrg(2, 18, 18), locrg(4, 13, 5, 11)));
    }

    public void testCodeChangedOneStatementToTwo() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) { i = 1; }",
                           "",
                           "}"),
                         
                 new Lines("class Test {" +
                           "" +
                           "    Test(int i) { ",
                           "        int j = 0;",
                           "        i = 2; ",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(CODE_CHANGED, "Test(int)", locrg(2, 19, 23), locrg(2, 9, 3, 13)));
    }

    public void testCodeChangedTwoStatementsToOne() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) { i = 1; int j = 2; }",
                           "",
                           "}"),
                         
                 new Lines("class Test {" +
                           "" +
                           "    Test(int i) { ",
                           "        int j = 0;",
                           "    }",
                           "}"),
                 
                 makeCodeChangedRef(CODE_CHANGED, "Test(int)", locrg(2, 19, 34), locrg(2, 9, 17)));
    }

    // do the same for 3:1, 3:2, 2:3, 1:3

    public void testCodeAddedOwnLine() {
        evaluate(new Lines("class Test {",
                           "    Test() { ",
                           "        char ch;",
                           "        int i = -1;",
                           "    }",
                           "",
                           "}"),
                 
                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        char ch;",
                           "        if (true) { }",
                           "        int i = -1;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(4, 9, 11), locrg(5, 9, 21)));
    }

    public void testCodeDeletedOneLine() {
        evaluate(new Lines("class Test {",
                           "    Test() { ",
                           "        int j = 0;",
                           "        int i = -1;",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { int i = -1; }",
                           "}"),
                 
                 // makeCodeDeletedRef(CODE_REMOVED, "Test()", locrg(3, 13, 4, 11), locrg(3, 18, 18)));
                 makeCodeDeletedRef(CODE_REMOVED, "Test()", locrg(3, 9, 18), locrg(3, 14, 16)));
    }

    public void testCodeDeletedTwoSequentialLines() {
        evaluate(new Lines("class Test {",
                           "    Test() { ",
                           "        int j = 0;",
                           "        double k;",
                           "        int i = -1;",
                           "    }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { int i = -1; }",
                           "}"),
                 
                 makeCodeDeletedRef(CODE_REMOVED, "Test()", locrg(3, 9, 4, 17), locrg(3, 14, 16)));
    }

    public void testCodeAddedTwoSequentialLines() {
        evaluate(new Lines("class Test {",
                           "    Test() { int i = -1; }",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test() { ",
                           "        int j = 0;",
                           "        double k;",
                           "        int i = -1;",
                           "    }",
                           "}"),
                 
                 makeCodeAddedRef(CODE_ADDED, "Test()", locrg(2, 14, 16), locrg(4, 9, 5, 17)));
    }
    
    public void testCodeInsertedAndChanged() {
        evaluate(new Lines("class Test {",
                           "    Test(int i) { i = 1; }",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    Test(int i) { ",
                           "        int j = 0;",
                           "        i = 2;",
                           "    }",
                           "}"),

                 makeCodeChangedRef(CODE_CHANGED, "Test(int)", locrg(2, 19, 23), locrg(4, 9, 5, 13)));
    }
}
