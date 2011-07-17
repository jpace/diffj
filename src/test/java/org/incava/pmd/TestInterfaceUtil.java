package org.incava.pmd;


public class TestInterfaceUtil extends TestTypeCase {

    public TestInterfaceUtil(String name) {
        super(name);
    }

    public void testTypeClass() {
        String contents0 = (
            "public interface Foo {\n" +
            "}\n"
            );

        String contents1 = (
            "abstract interface Foo {\n" +
            "}\n"
            );

        runTest(contents0, contents1, 1.0);
    }

}
