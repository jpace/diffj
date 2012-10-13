package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.pmdx.FieldUtil;

public class Fields extends Items<Field, ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
    }

    public Field getAstType(ASTFieldDeclaration fieldDecl) {
        return new Field(fieldDecl);
    }

    public String getName(ASTFieldDeclaration field) {
        return FieldUtil.getNames(field);
    }

    public String getAddedMessage(ASTFieldDeclaration field) {
        return Messages.FIELD_ADDED;
    }

    public String getRemovedMessage(ASTFieldDeclaration field) {
        return Messages.FIELD_REMOVED;
    }
}
