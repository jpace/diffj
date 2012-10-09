package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeFields extends Items<ASTFieldDeclaration> {
    private final FileDiffs fileDiffs;

    public TypeFields(FileDiffs fileDiffs) {
        super(fileDiffs, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
        this.fileDiffs = fileDiffs;
    }    

    public void doCompare(ASTFieldDeclaration fromField, ASTFieldDeclaration toField) {
        Field field = new Field(fromField);
        field.compareAccess(SimpleNodeUtil.getParent(fromField), SimpleNodeUtil.getParent(toField), differences);
        field.diff(toField, differences);
    }

    public String getName(ASTFieldDeclaration fd) {
        return FieldUtil.getNames(fd);
    }

    public String getAddedMessage(ASTFieldDeclaration fd) {
        return Messages.FIELD_ADDED;
    }

    public String getRemovedMessage(ASTFieldDeclaration fd) {
        return Messages.FIELD_REMOVED;
    }

    public double getScore(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        return FieldUtil.getMatchScore(a, b);
    }
}
