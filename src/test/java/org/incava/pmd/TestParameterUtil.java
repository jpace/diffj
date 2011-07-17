package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestParameterUtil extends TestUtil {
    
    public TestParameterUtil(String name) {
        super(name);
    }

    protected ASTConstructorDeclaration[] parseCtors(String contents, int numParameters) {
        return (ASTConstructorDeclaration[])parse(contents, numParameters, ASTConstructorDeclaration.class);
    }

    protected void runCtorTest(ASTConstructorDeclaration ctor,
                               String[] pTypes,
                               String[] pNames) {
        ASTFormalParameters params = CtorUtil.getParameters(ctor);
        assertNotNull("params", params);

        ASTFormalParameter[] plist = ParameterUtil.getParameters(params);
        assertNotNull("plist", plist);
        assertEquals("#plist", pTypes.length, plist.length);

        List<String> ptlist = ParameterUtil.getParameterTypes(params);
        assertNotNull("ptlist", ptlist);
        assertEquals("#ptlist", pTypes.length, ptlist.size());

        Token[] pnames = ParameterUtil.getParameterNames(params);
        assertNotNull("pnames", pnames);
        assertEquals("#pnames", pNames.length, pnames.length);

        for (int pi = 0; pi < plist.length; ++pi) {
            assertEquals("pnames[" + pi + "]", pNames[pi], pnames[pi].image);

            ASTFormalParameter fp = ParameterUtil.getParameter(params, pi);
            SimpleNodeUtil.dump(fp, "");
            assertNotNull("fp", fp);
            
            Token pn = ParameterUtil.getParameterName(params, pi);
            assertNotNull("pn", pn);
            assertEquals("pn.image", pNames[pi], pn.image);
            
            pn = ParameterUtil.getParameterName(fp);
            tr.Ace.log("pn", pn);
            assertNotNull("pn", pn);
            assertEquals("pn.image", pNames[pi], pn.image);
            
            String ty = ParameterUtil.getParameterType(params, pi);
            tr.Ace.log("ty", ty);
            assertNotNull("ty", ty);
            assertEquals("ty", pTypes[pi], ty);
        
            ty = ParameterUtil.getParameterType(fp);
            assertNotNull("ty", ty);
            assertEquals("ty", pTypes[pi], ty);
        }
    }
    
    public void testCtors() {
        String contents = (
            "public class Foo {\n" +
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char ch) {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(String[] strs, Object obj[]) {\n" +
            "    }\n" +
            "}\n"
            );

        ASTConstructorDeclaration[] ctors = parseCtors(contents, 3);
        
        runCtorTest(ctors[0], new String[] {}, new String[] {});

        runCtorTest(ctors[1], 
                    new String[] {
                        "char"
                    },
                    new String[] {
                        "ch"
                    });

        runCtorTest(ctors[2], 
                    new String[] {
                        "String[]",
                        "Object[]"
                    },
                    new String[] {
                        "strs",
                        "obj"
                    });

    }

    protected void runMatchTest(ASTConstructorDeclaration ctor0, 
                                ASTConstructorDeclaration ctor1, 
                                double expected) {
        ASTFormalParameters params0 = CtorUtil.getParameters(ctor0);
        ASTFormalParameters params1 = CtorUtil.getParameters(ctor1);

        double score = ParameterUtil.getMatchScore(params0, params1);

        assertEquals("score", expected, score, 0.0001);
    }
    
    public void testMatches() {
        // Of course, this is invalid Java, but it parses and that's all we
        // need. That is, it doesn't need to be from separate compilation units.

        String contents = (
            "public class Foo {\n" +
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char ch) {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char c) {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(String[] strs, Object obj[]) {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(Object obj[], String[] strs) {\n" +
            "    }\n" +
            "}\n"
            );

        ASTConstructorDeclaration[] ctors = parseCtors(contents, 6);
        
        runMatchTest(ctors[0], ctors[1], 1.0);
        runMatchTest(ctors[0], ctors[2], 0.5);
        runMatchTest(ctors[0], ctors[3], 0.5);
        runMatchTest(ctors[0], ctors[4], 0.5);
        runMatchTest(ctors[0], ctors[5], 0.5);

        runMatchTest(ctors[1], ctors[2], 0.5);
        runMatchTest(ctors[1], ctors[3], 0.5);
        runMatchTest(ctors[1], ctors[4], 0.5);
        runMatchTest(ctors[1], ctors[5], 0.5);

        runMatchTest(ctors[2], ctors[3], 1.0);
        runMatchTest(ctors[2], ctors[4], 0.5);
        runMatchTest(ctors[2], ctors[5], 0.5);

        runMatchTest(ctors[3], ctors[4], 0.5);
        runMatchTest(ctors[3], ctors[5], 0.5);

        runMatchTest(ctors[4], ctors[5], 0.75);
    }

}
