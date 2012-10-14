package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

public class Fields extends Items<Field, ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
    }

    public Field getAstType(ASTFieldDeclaration fieldDecl) {
        return new Field(fieldDecl);
    }
}
