package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.pmdx.FieldUtil;

public class Fields extends Items<ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
    }

    public void doCompare(ASTFieldDeclaration fromFieldDecl, ASTFieldDeclaration toFieldDecl, Differences differences) {
        doCompare(new Field(fromFieldDecl), new Field(toFieldDecl), differences);
    }

    public void doCompare(Field fromField, Field toField, Differences differences) {
        fromField.diff(toField, differences);
    }

    public double getScore(ASTFieldDeclaration fromFieldDecl, ASTFieldDeclaration toFieldDecl) {
        return getScore(new Field(fromFieldDecl), new Field(toFieldDecl));
    }

    public double getScore(Field fromField, Field toField) {
        return fromField.getMatchScore(toField);
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
