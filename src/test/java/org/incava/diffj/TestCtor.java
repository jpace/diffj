package org.incava.diffj;

import java.text.MessageFormat;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.java.Java;

public class TestCtor extends ItemsTest {
    protected final static String[] THROWS_MSGS = new String[] {
        Messages.THROWS_REMOVED,
        null,
        Messages.THROWS_ADDED,
    };

    public TestCtor(String name) {
        super(name);
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
                 
                 makeCodeChangedRef(Messages.CODE_CHANGED, "Test()", loc(2, 23), loc(2, 23), loc(4, 18), loc(4, 18)));
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
                 
                 makeCodeAddedRef(Messages.CODE_ADDED, "Test()", loc(2, 18), loc(2, 18), loc(4, 13), loc(5, 11)));
    }

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
                 
                 makeCodeAddedRef(Messages.CODE_ADDED, "Test()", loc(4, 9), loc(4, 11), loc(5, 9), loc(5, 21)));
    }

    public void testCodeDeleted() {
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
                 
                 makeCodeDeletedRef(Messages.CODE_REMOVED, "Test()", loc(3, 13), loc(4, 11), loc(3, 18), loc(3, 18)));
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

                 makeCodeChangedRef(Messages.CODE_CHANGED, "Test(int)", loc(2, 19), loc(2, 23), loc(4,  9), loc(5, 13)));
    }

    public void testCtorWithParameterizedTypeDump() {
        Lines lines = new Lines("public class LogIterator {",
                                "    public <T> LogIterator() {",
                                "    }",
                                "}");
        
        tr.Ace.setVerbose(true);
        tr.Ace.yellow("this", this);

        try {
            JavaFile jf = new JavaFile("name", lines.toString(), "1.6");
            org.incava.pmdx.SimpleNodeUtil.dump(jf.compile());
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        
        evaluate(lines, lines, NO_CHANGES);
    }

    public void testCtorWithoutParameterizedTypeDump() {
        Lines lines = new Lines("public class LogIterator {",
                                "    public LogIterator() {",
                                "    }",
                                "}");
        
        tr.Ace.setVerbose(true);
        tr.Ace.yellow("this", this);

        try {
            JavaFile jf = new JavaFile("name", lines.toString(), "1.6");
            org.incava.pmdx.SimpleNodeUtil.dump(jf.compile());
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        
        evaluate(lines, lines, NO_CHANGES);
    }
        
    public void testCtorWithParameterizedType() {
        tr.Ace.setVerbose(true);
        tr.Ace.yellow("this", this);
        evaluate(new Lines("public class LogIterator {",
                           "    public <T> LogIterator() {",
                           "    }",
                           "}"),
                 new Lines("public class LogIterator {",
                           "    public <T> LogIterator() {",
                           "    }",
                           "}"),
                 Java.SOURCE_1_6,
                 NO_CHANGES);
    }

    protected String paramReordMsg(String paramName, int oldPosition, int newPosition) {
        return MessageFormat.format(Messages.PARAMETER_REORDERED, paramName, Integer.valueOf(oldPosition), Integer.valueOf(newPosition));
    }

    protected String paramReordRenamedMsg(String oldName, int oldPosition, String newName, int newPosition) {
        return MessageFormat.format(Messages.PARAMETER_REORDERED_AND_RENAMED, oldName, Integer.valueOf(oldPosition), Integer.valueOf(newPosition), newName);
    }

    protected String throwsReordMsg(String throwsName, int oldPosition, int newPosition) {
        return MessageFormat.format(Messages.THROWS_REORDERED, throwsName, Integer.valueOf(oldPosition), Integer.valueOf(newPosition));
    }

    protected FileDiff makeChangedRef(String msg, 
                                      String fromStr, String toStr,
                                      Location fromStart, Location toStart) {
        return new FileDiffChange(getMessage(null, null, msg, fromStr, toStr), fromStart, loc(fromStart, fromStr), toStart, loc(toStart, toStr));
    }
}
