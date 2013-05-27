package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.java.Java;

public class TestCtor extends ItemsTest {
    public TestCtor(String name) {
        super(name);
        tr.Ace.setVerbose(true);
        tr.Ace.yellow("name", name);
    }

    public void testCtorWithParameterizedTypeDump() {
        Lines lines = new Lines("public class LogIterator<T> {",
                                "    public <T> LogIterator() {",
                                "    }",
                                "}");
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
        //$$$ todo: fix this diff to show the type parameter removed:
        evaluate(new Lines("public class LogIterator<T> {",
                           "    public LogIterator() {",
                           "    }",
                           "}"),
                 new Lines("public class LogIterator {",
                           "    public LogIterator() {",
                           "    }",
                           "}"),
                 Java.SOURCE_1_6,
                 NO_CHANGES);
    }
}
