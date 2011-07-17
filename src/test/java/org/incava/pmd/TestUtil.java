package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestUtil extends TestCase {
    
    public TestUtil(String name) {
        super(name);
    }

    protected SimpleNode[] parse(String contents, int numDecls, Class declType) {
        JavaCharStream jcs = new JavaCharStream(new StringReader(contents));
        JavaParser p = new JavaParser(jcs);

        ASTCompilationUnit cu = p.CompilationUnit();

        // SimpleNodeUtil.dump(cu);

        ASTTypeDeclaration[] tdecls = CompilationUnitUtil.getTypeDeclarations(cu);
        ASTTypeDeclaration tdecl = tdecls[0];
        assertNotNull(tdecl);

        ASTClassOrInterfaceDeclaration coid = (ASTClassOrInterfaceDeclaration)SimpleNodeUtil.findChild(tdecl, ASTClassOrInterfaceDeclaration.class);
        assertNotNull(coid);

        ASTClassOrInterfaceBody body = (ASTClassOrInterfaceBody)SimpleNodeUtil.findChild(coid, ASTClassOrInterfaceBody.class);
        assertNotNull(body);

        ASTClassOrInterfaceBodyDeclaration[] coibds = (ASTClassOrInterfaceBodyDeclaration[])SimpleNodeUtil.findChildren(body, ASTClassOrInterfaceBodyDeclaration.class);
        
        assertNotNull(coibds);
        assertEquals("#decls", numDecls, coibds.length);
        
        List<SimpleNode> declList = new ArrayList<SimpleNode>();

        for (int ci = 0; ci < coibds.length; ++ci) {
            ASTClassOrInterfaceBodyDeclaration coibd = coibds[ci];
            // tr.Ace.log("coibd", coibd);

            SimpleNodeUtil.dump(coibd);
            
            SimpleNode decl = SimpleNodeUtil.findChild(coibd, declType);
            // tr.Ace.log("declType", declType);
            // tr.Ace.log("decl", decl);
            
            assertNotNull("decl[" + ci + "]", decl);
            declList.add(decl);
        }

        assertEquals("#decls", numDecls, declList.size());

        int size = declList.size();
        SimpleNode[] ary = (SimpleNode[])java.lang.reflect.Array.newInstance(declType, size);
        System.arraycopy(declList.toArray(), 0, ary, 0, size);
        return ary;
    }

    public void testNothing() {
        // nothing.
    }
}
