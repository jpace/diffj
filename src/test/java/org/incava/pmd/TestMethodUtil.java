package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestMethodUtil extends TestUtil {

    public TestMethodUtil(String name) {
        super(name);
    }

    protected ASTMethodDeclaration[] parse(String contents, int numMeths) {
        return (ASTMethodDeclaration[])parse(contents, numMeths, ASTMethodDeclaration.class);
    }

    public void testMeths() {
        String contents = (
            "public class Foo {\n" +
            "    public void foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public void foo(char ch) {\n" +
            "    }\n" +
            "}\n"
            );

        ASTMethodDeclaration[] meths = parse(contents, 2);
        
        ASTMethodDeclaration meth0 = meths[0];
        Token ntk0 = MethodUtil.getName(meth0);
        assertNotNull("ntk", ntk0);
        assertEquals("foo", ntk0.image);

        ASTFormalParameters params0 = MethodUtil.getParameters(meth0);
        assertNotNull("params0", params0);

        assertEquals("fullname0", "foo()", MethodUtil.getFullName(meth0));

        ASTMethodDeclaration meth1 = meths[1];
        Token ntk1 = MethodUtil.getName(meth1);
        assertNotNull("ntk1", ntk1);
        assertEquals("foo", ntk1.image);

        ASTFormalParameters params1 = MethodUtil.getParameters(meth1);
        assertNotNull("params1", params1);

        String fnm = MethodUtil.getFullName(meth1);
        assertEquals("fullname1", "foo(char)", fnm);
        
        double mscore = MethodUtil.getMatchScore(meth0, meth1);
        assertEquals("match score", 0.5, mscore, 0.0001);
    }

    public void testThrows() {
        String contents = (
            "public class Foo {\n" +
            "    public void foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public void foo(int i) throws IOException {\n" +
            "    }\n" +
            "    \n" + 
            "    public void foo(char ch) throws NullPointerException,\n" +
            "         ArrayIndexOutOfBoundsException\n" +
            "    {\n" +
            "    }\n" +
            "}\n"
            );

        ASTMethodDeclaration[] meths = parse(contents, 3);
        
        ASTMethodDeclaration meth0 = meths[0];
        Token tk0 = MethodUtil.getThrows(meth0);
        assertNull("tk0", tk0);
        ASTNameList nl0 = MethodUtil.getThrowsList(meth0);
        assertNull("nl0", nl0);

        ASTMethodDeclaration meth1 = meths[1];
        Token tk1 = MethodUtil.getThrows(meth1);
        assertNotNull("tk1", tk1);
        ASTNameList nl1 = MethodUtil.getThrowsList(meth1);
        assertNotNull("nl1", nl1);        

        ASTMethodDeclaration meth2 = meths[2];
        Token tk2 = MethodUtil.getThrows(meth2);
        assertNotNull("tk2", tk2);
        ASTNameList nl2 = MethodUtil.getThrowsList(meth2);
        assertNotNull("nl2", nl2);
    }

    public void testScore() {
        String contents = (
            "public class Foo {\n" +
            "    public void foo() {\n" +
            "    }\n" +
            "    \n" + 
            "    public void foo(int i) throws IOException {\n" +
            "    }\n" +
            "    \n" + 
            "    public void foo(int i, char ch) throws NullPointerException,\n" +
            "         ArrayIndexOutOfBoundsException\n" +
            "    {\n" +
            "    }\n" +
            "}\n"
            );

        ASTMethodDeclaration[] meths = parse(contents, 3);
        
        ASTMethodDeclaration meth0 = meths[0];
        ASTMethodDeclaration meth1 = meths[1];
        ASTMethodDeclaration meth2 = meths[2];

        double ms01 = MethodUtil.getMatchScore(meth0, meth1);
        assertEquals("score 0-1", 0.5, ms01, 0.00001);
        
        double ms12 = MethodUtil.getMatchScore(meth1, meth2);
        assertEquals("score 1-2", 0.75, ms12, 0.00001);

        double ms02 = MethodUtil.getMatchScore(meth0, meth2);
        assertEquals("score 0-2", 0.5, ms02, 0.00001);
    }

}
