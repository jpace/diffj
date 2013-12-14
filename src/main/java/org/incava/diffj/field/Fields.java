package org.incava.diffj.field;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.diffj.type.Items;

public class Fields extends Items<Field, ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration typeDecl) {
        super(typeDecl, ASTFieldDeclaration.class);
    }

    public Field getAstType(ASTFieldDeclaration fieldDecl) {
        return new Field(fieldDecl);
    }
}
