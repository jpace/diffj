package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


/**
 * Test case for types (classes and interfaces). Not the same as
 * TestTypeDeclaration, although they might be merged.
 */
public class TestTypeCase extends TestCase {

    public TestTypeCase(String name) {
        super(name);
    }

    protected ASTCompilationUnit parse(String contents) {
        JavaCharStream jcs = new JavaCharStream(new StringReader(contents));
        JavaParser p = new JavaParser(jcs);

        ASTCompilationUnit cu = p.CompilationUnit();
        return cu;
    }

    public void testNothing() {
        // this is something ... this is nothing ... which keeps JUnit from
        // complaining that there are no tests in this class.
    }

    protected ASTClassOrInterfaceDeclaration runTypeTest(String contents) {
        ASTCompilationUnit cu = parse(contents);

        assertNotNull("compilation unit", cu);
        ASTPackageDeclaration pkg = CompilationUnitUtil.getPackage(cu);
        assertNull("pkg", pkg);

        ASTImportDeclaration[] imports = CompilationUnitUtil.getImports(cu);
        assertNotNull("imports", imports);
        assertEquals("imports length", 0, imports.length);

        ASTTypeDeclaration[] decls = CompilationUnitUtil.getTypeDeclarations(cu);
        assertNotNull("decls", decls);
        assertEquals("decls length", 1, decls.length);

        ASTTypeDeclaration decl = decls[0];
        Token ntk = TypeDeclarationUtil.getName(decl);
        assertNotNull("ntk", ntk);
        assertEquals("ntk", "Foo", ntk.image);

        ASTClassOrInterfaceDeclaration coid = (ASTClassOrInterfaceDeclaration)SimpleNodeUtil.findChild(decl, ASTClassOrInterfaceDeclaration.class);
        assertNotNull("coid", coid);
        
        Token id0 = InterfaceUtil.getName(coid);
        assertNotNull("id0", id0);
        assertEquals("id0 type", JavaParserConstants.IDENTIFIER, id0.kind);
        assertEquals("id0 string", "Foo", id0.toString());
        
        Token id1 = ClassUtil.getName(coid);
        assertNotNull("id1", id0);
        assertEquals("id1 type", JavaParserConstants.IDENTIFIER, id1.kind);
        assertEquals("id1 string", "Foo", id1.toString());

        assertEquals("id0 <=> id1", id0, id1);

        return coid;
    }

    protected void runTest(String contents0, String contents1, double expScore) {
        ASTClassOrInterfaceDeclaration coid0 = runTypeTest(contents0);
        ASTClassOrInterfaceDeclaration coid1 = runTypeTest(contents1);
        
        double mscore = InterfaceUtil.getMatchScore(coid0, coid1);
        assertEquals("match score", expScore, mscore, 0.0001);
    }

}
