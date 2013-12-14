package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import org.incava.diffj.type.Items;

public class Ctors extends Items<Ctor, ASTConstructorDeclaration> {
    public Ctors(ASTClassOrInterfaceDeclaration typeDecl) {
        super(typeDecl, ASTConstructorDeclaration.class);
    }

    public Ctor getAstType(ASTConstructorDeclaration ctorDecl) {
        return new Ctor(ctorDecl);
    }
}
