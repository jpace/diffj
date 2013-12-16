package org.incava.diffj.function;

import org.incava.diffj.ItemsTest;
import org.incava.diffj.io.JavaFile;
import org.incava.diffj.util.Lines;
import org.incava.java.Java;

public class TestCtor extends ItemsTest {
    public TestCtor(String name) {
        super(name);
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
