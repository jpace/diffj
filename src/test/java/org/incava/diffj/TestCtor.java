package org.incava.diffj;

import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.java.Java;

public class TestCtor extends ItemsTest {
    public TestCtor(String name) {
        super(name);
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
}
