package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestCompilationUnitUtil extends TestCase {

    public TestCompilationUnitUtil(String name) {
        super(name);
    }

    protected ASTCompilationUnit parse(String contents) {
        JavaCharStream jcs = new JavaCharStream(new StringReader(contents));
        JavaParser p = new JavaParser(jcs);

        ASTCompilationUnit cu = p.CompilationUnit();
        return cu;
    }

    public ASTCompilationUnit runTest(String contents, boolean hasPkg, int numImports, int numTypes) {
        ASTCompilationUnit cu = parse(contents);

        assertNotNull("compilation unit", cu);
        ASTPackageDeclaration pkg = CompilationUnitUtil.getPackage(cu);
        tr.Ace.log("pkg", pkg);
        assertTrue("pkg", hasPkg == (pkg != null));

        ASTImportDeclaration[] imports = CompilationUnitUtil.getImports(cu);
        tr.Ace.log("imports", imports);
        assertNotNull("imports", imports);
        assertEquals("imports length", numImports, imports.length);
        for (int ii = 0; ii < imports.length; ++ii) {
            ASTImportDeclaration impt = imports[ii];
            assertNotNull("impt[" + ii + "]", impt);
        }

        ASTTypeDeclaration[] decls = CompilationUnitUtil.getTypeDeclarations(cu);
        tr.Ace.log("decls", decls);
        assertNotNull("decls", decls);
        assertEquals("decls length", numTypes, decls.length);

        for (int di = 0; di < decls.length; ++di) {
            ASTTypeDeclaration decl = decls[di];
            assertNotNull("decl[" + di + "]", decl);
        }
        
        return cu;
    }

    public void testSingleType() {
        String contents = (
            "public interface Foo {\n" +
            "}\n" +
            "\n"
            );

        runTest(contents, false, 0, 1);
    }

    public void testTwoTypes() {
        String contents = (
            "public interface Foo {\n" +
            "}\n" +
            "\n" +
            "class Bar {\n" +
            "}\n" +
            "\n"
            );

        runTest(contents, false, 0, 2);
    }

    public void testPackage() {
        String contents = (
            "package org.incava.foo.bar;\r\n" +
            "\n"
            );

        runTest(contents, true, 0, 0);
    }

    public void testImportOne() {
        String contents = (
            "import org.incava.foo.Bar;\r\n" +
            "\n"
            );

        runTest(contents, false, 1, 0);
    }

    public void testImportTwo() {
        String contents = (
            "import org.incava.foo.Bar;\r\n" +
            "import org.incava.baz.*;\r\n" +
            "\n"
            );

        runTest(contents, false, 2, 0);
    }

    public void testAll() {
        String contents = (
            "package org.incava.foo.bar;\r\n" +
            "\n" +
            "import org.incava.foo.Bar;\r\n" +
            "import org.incava.baz.*;\r\n" +
            "\n" +
            "public interface Foo {\n" +
            "}\n" +
            "\n" +
            "class Bar {\n" +
            "}\n" +
            "\n"
            );

        runTest(contents, true, 2, 2);
    }

    public void testSemicolonOnly() {
        // yes, this is a valid compilation unit, with one type.
        String contents = (
            ";\n"
            );

        runTest(contents, false, 0, 1);
    }

    public void testEmpty() {
        // and yes, this, too, is a valid compilation unit.
        String contents = (
            "\n"
            );

        runTest(contents, false, 0, 0);
    }

}
