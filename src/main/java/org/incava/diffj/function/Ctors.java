package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import org.incava.diffj.Items;

public class Ctors extends Items<Ctor, ASTConstructorDeclaration> {
    public Ctors(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTConstructorDeclaration");
    }    

    public Ctor getAstType(ASTConstructorDeclaration ctorDecl) {
        return new Ctor(ctorDecl);
    }
}
