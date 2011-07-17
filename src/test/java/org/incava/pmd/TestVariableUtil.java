package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestVariableUtil extends TestUtil {

    public TestVariableUtil(String name) {
        super(name);
    }

    protected ASTFieldDeclaration[] parse(String contents, int numFields) {
        return (ASTFieldDeclaration[])parse(contents, numFields, ASTFieldDeclaration.class);
    }

    protected void checkVD(ASTFieldDeclaration fld, String[] names) {
        ASTVariableDeclarator[] avds = FieldUtil.getVariableDeclarators(fld);
        assertEquals("#avds", names.length, avds.length);

        Token[] tknames = VariableUtil.getVariableNames(avds);
        assertNotNull("tknames", tknames);
        assertEquals("#tknames", names.length, tknames.length);
        
        for (int ai = 0; ai < avds.length; ++ai) {
            ASTVariableDeclarator avd = avds[ai];
            assertNotNull("avd[" + ai + "]", avd);

            Token tknm = VariableUtil.getName(avd);
            assertNotNull("avd[" + ai + "]", tknm);

            assertSame("avd[" + ai + "]", tknames[ai], tknm);

            assertEquals("avd[" + ai + "]", names[ai], tknm.image);
            assertEquals("avd[" + ai + "]", names[ai], tknames[ai].image);
        }
    }

    public void testFields() {
        String contents = (
            "public class Foo {\n" +
            "    public int x;\n" +
            "    \n" + 
            "    char c, ch;\n" +
            "    \n" + 
            "    char d, dh = 'x';\n" +
            "    \n" + 
            "    String s[] = new String[] { \"fee\", \"fi\", \"fo\", \"fum\" };\n" +
            "}\n"
            );

        ASTFieldDeclaration[] flds = parse(contents, 4);

        tr.Ace.log("flds", flds);

        checkVD(flds[0], new String[] { "x" });
        checkVD(flds[1], new String[] { "c", "ch" });
        checkVD(flds[2], new String[] { "d", "dh" });
        checkVD(flds[3], new String[] { "s" });
    }

}
