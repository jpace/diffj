package org.incava.pmd;


public class TestClassUtil extends TestTypeCase {
    
    public TestClassUtil(String name) {
        super(name);
    }

    public void testTypeClass() {
        String contents0 = (
            "public abstract strictfp final class Foo {\n" +
            "}\n"
            );

        String contents1 = (
            "abstract class Foo {\n" +
            "}\n"
            );
        
        runTest(contents0, contents1, 1.0);
    }
}
