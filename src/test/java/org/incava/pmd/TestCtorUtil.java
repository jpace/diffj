package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestCtorUtil extends TestUtil {
    
    public TestCtorUtil(String name) {
        super(name);
    }

    protected ASTConstructorDeclaration[] parse(String contents, int numCtors) {
        return (ASTConstructorDeclaration[])parse(contents, numCtors, ASTConstructorDeclaration.class);
    }

    public void testCtors() {
        String contents = (
            "public class Foo {\n" +
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char ch) {\n" +
            "    }\n" +
            "}\n"
            );

        ASTConstructorDeclaration[] ctors = parse(contents, 2);
        
        ASTConstructorDeclaration ctor0 = ctors[0];
        Token ntk0 = CtorUtil.getName(ctor0);
        assertNotNull("ntk", ntk0);
        assertEquals("Foo", ntk0.image);

        ASTFormalParameters params0 = CtorUtil.getParameters(ctor0);
        assertNotNull("params0", params0);

        assertEquals("fullname0", "Foo()", CtorUtil.getFullName(ctor0));

        ASTConstructorDeclaration ctor1 = ctors[1];
        Token ntk1 = CtorUtil.getName(ctor1);
        assertNotNull("ntk1", ntk1);
        assertEquals("Foo", ntk1.image);

        ASTFormalParameters params1 = CtorUtil.getParameters(ctor1);
        assertNotNull("params1", params1);

        String fnm = CtorUtil.getFullName(ctor1);
        assertEquals("fullname1", "Foo(char)", fnm);
        
        double mscore = CtorUtil.getMatchScore(ctor0, ctor1);
        tr.Ace.log("mscore: " + mscore);
        assertEquals("match score", 0.5, mscore, 0.0001);
    }

    public void testThrows() {
        String contents = (
            "public class Foo {\n" +
            "    public Foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(int i) throws IOException {\n" +
            "    }\n" +
            "    \n" + 
            "    public Foo(char ch) throws NullPointerException,\n" +
            "         ArrayIndexOutOfBoundsException\n" +
            "    {\n" +
            "    }\n" +
            "}\n"
            );

        ASTConstructorDeclaration[] ctors = parse(contents, 3);
        
        ASTConstructorDeclaration ctor0 = ctors[0];
        Token tk0 = CtorUtil.getThrows(ctor0);
        assertNull("tk0", tk0);
        ASTNameList nl0 = CtorUtil.getThrowsList(ctor0);
        assertNull("nl0", nl0);

        ASTConstructorDeclaration ctor1 = ctors[1];
        Token tk1 = CtorUtil.getThrows(ctor1);
        assertNotNull("tk1", tk1);
        ASTNameList nl1 = CtorUtil.getThrowsList(ctor1);
        tr.Ace.log("nl1", nl1);
        assertNotNull("nl1", nl1);        

        ASTConstructorDeclaration ctor2 = ctors[2];
        Token tk2 = CtorUtil.getThrows(ctor2);
        assertNotNull("tk2", tk2);
        ASTNameList nl2 = CtorUtil.getThrowsList(ctor2);
        tr.Ace.log("nl2", nl2);
        assertNotNull("nl2", nl2);
    }

}
