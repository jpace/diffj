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

    public String getName(ASTFieldDeclaration fieldDecl) {
        Field field = new Field(fieldDecl);
        return field.getName();
    }

    public String getAddedMessage(ASTFieldDeclaration fieldDecl) {
        Field field = new Field(fieldDecl);
        return field.getAddedMessage();
    }

    public String getRemovedMessage(ASTFieldDeclaration fieldDecl) {
        Field field = new Field(fieldDecl);
        return field.getRemovedMessage();
    }
}
