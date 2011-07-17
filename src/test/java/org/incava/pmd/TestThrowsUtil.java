package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestThrowsUtil extends TestUtil {
    
    public TestThrowsUtil(String name) {
        super(name);
    }

    protected ASTConstructorDeclaration[] parseCtors(String contents, int numThrowss) {
        return (ASTConstructorDeclaration[])parse(contents, numThrowss, ASTConstructorDeclaration.class);
    }

    protected void runCtorTest(ASTConstructorDeclaration ctor, String[] throwsNames) {
        Token tk = CtorUtil.getThrows(ctor);
        assertTrue("tk", (throwsNames.length > 0) == (tk != null));

        ASTNameList nlist = CtorUtil.getThrowsList(ctor);
        assertTrue("nlist", (throwsNames.length > 0) == (nlist != null));

        if (throwsNames.length > 0) {
            ASTName[] names = ThrowsUtil.getNames(nlist);
            tr.Ace.log("names", names);

            assertEquals("#names", throwsNames.length, names.length);

            for (int ni = 0; ni < names.length; ++ni) {
                ASTName name = names[ni];
                tr.Ace.log("name", name);

                String nmstr = SimpleNodeUtil.toString(name);
                assertEquals("name[" + ni + "]", throwsNames[ni], nmstr);

                String nm1 = ThrowsUtil.getName(nlist, ni);
                assertEquals("name[" + ni + "]", throwsNames[ni], nm1);
            }
        }
    }
    
    public void testCtors() {
        String contents = (
            "public class Foo {\n" +
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char ch) throws EIEIOException {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(String[] strs, Object obj[]) throws NullNoPointersInJavaException, EIEIOException {\n" +
            "    }\n" +
            "}\n"
            );

        ASTConstructorDeclaration[] ctors = parseCtors(contents, 3);
        
        runCtorTest(ctors[0], new String[] {});
        runCtorTest(ctors[1], new String[] { "EIEIOException" });
        runCtorTest(ctors[2], new String[] { "NullNoPointersInJavaException", "EIEIOException" });
    }

}
