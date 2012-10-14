package org.incava.diffj.field;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.diffj.Items;

public class Fields extends Items<Field, ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
    }

    public Field getAstType(ASTFieldDeclaration fieldDecl) {
        return new Field(fieldDecl);
    }
}
