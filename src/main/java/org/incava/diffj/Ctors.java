package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;

public class Ctors extends Items<Ctor, ASTConstructorDeclaration> {
    public Ctors(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTConstructorDeclaration");
    }    

    public Ctor getAstType(ASTConstructorDeclaration ctorDecl) {
        return new Ctor(ctorDecl);
    }

    public String getName(ASTConstructorDeclaration ctorDecl) {
        Ctor ctor = new Ctor(ctorDecl);
        return ctor.getName();
    }

    public String getAddedMessage(ASTConstructorDeclaration ctorDecl) {
        Ctor ctor = new Ctor(ctorDecl);
        return ctor.getAddedMessage();
    }

    public String getRemovedMessage(ASTConstructorDeclaration ctorDecl) {
        Ctor ctor = new Ctor(ctorDecl);
        return ctor.getRemovedMessage();
    }
}
