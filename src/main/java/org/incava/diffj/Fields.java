package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Fields extends Items<ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type, Differences differences) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration", differences);
    }

    public void doCompare(ASTFieldDeclaration fromField, ASTFieldDeclaration toField) {
        Field field = new Field(fromField);
        field.compareAccess(SimpleNodeUtil.getParent(fromField), SimpleNodeUtil.getParent(toField), differences);
        field.diff(toField, differences);
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

    public double getScore(ASTFieldDeclaration fromField, ASTFieldDeclaration toField) {
        return FieldUtil.getMatchScore(fromField, toField);
    }
}
