package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestFieldUtil extends TestUtil {
    
    public TestFieldUtil(String name) {
        super(name);

        tr.Ace.setVerbose(true);
        
    }

    protected ASTFieldDeclaration[] parse(String contents, int numFields) {
        return (ASTFieldDeclaration[])parse(contents, numFields, ASTFieldDeclaration.class);
    }

    protected void checkVD(ASTFieldDeclaration fld, int numExpected) {
        ASTVariableDeclarator[] avds = FieldUtil.getVariableDeclarators(fld);

        assertEquals("#avds", numExpected, avds.length);
        for (int ai = 0; ai < avds.length; ++ai) {
            ASTVariableDeclarator avd = avds[ai];
            assertNotNull("avd[" + ai + "]", avd);
        }
    }

    public void xtestFields() {
        String contents = (
            "public class Foo {\n" +
            "    public int x;\n" +
            "    \n" + 
            "    char c, ch;\n" +
            "    \n" + 
            "    char d, dh;\n" +
            "}\n"
            );

        ASTFieldDeclaration[] flds = parse(contents, 3);
        
        ASTFieldDeclaration fld0 = flds[0];
        checkVD(fld0, 1);

        ASTFieldDeclaration fld1 = flds[1];
        checkVD(fld1, 2);

        double mscore01 = FieldUtil.getMatchScore(fld0, fld1);
        assertEquals("score 0-1", 0.0, mscore01, 0.0001);

        ASTFieldDeclaration fld2 = flds[2];
        checkVD(fld2, 2);

        double mscore12 = FieldUtil.getMatchScore(fld1, fld2);
        assertEquals("score 1-2", 0.5, mscore12, 0.0001);

        double mscore02 = FieldUtil.getMatchScore(fld0, fld2);
        assertEquals("score 0-2", 0.0, mscore02, 0.0001);
    }

    public void testFieldScore() {
        String strOne = ("public class Collections {\n" +
                         "\n" +
                         "    private final Object k, v;\n" +
                         "}\n");

        String strTwo = ("public class Collections {\n" +
                         "\n" +
                         "    private final K k;\n" +
                         "    private final V v;\n" +
                         "}\n");

        tr.Ace.setVerbose(true);

        ASTFieldDeclaration[] fldsOne = parse(strOne, 1);
        tr.Ace.yellow("fldsOne", fldsOne);

        for (ASTFieldDeclaration fd : fldsOne) {
            tr.Ace.yellow("fd", fd);

            SimpleNodeUtil.dump(fd);
        }

        ASTFieldDeclaration[] fldsTwo = parse(strTwo, 2);
        tr.Ace.yellow("fldsTwo", fldsTwo);

        for (ASTFieldDeclaration fd : fldsTwo) {
            tr.Ace.yellow("fd", fd);

            SimpleNodeUtil.dump(fd);
        }

        double kkScore = FieldUtil.getMatchScore(fldsOne[0], fldsTwo[0]);
        tr.Ace.log("kkScore", String.valueOf(kkScore));

        double kvScore = FieldUtil.getMatchScore(fldsOne[0], fldsTwo[1]);
        tr.Ace.log("kvScore", String.valueOf(kvScore));
    }

}
