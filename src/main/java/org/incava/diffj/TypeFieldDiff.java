package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeFieldDiff extends TypeItem<ASTFieldDeclaration> {
    private final FileDiffs fileDiffs;

    public TypeFieldDiff(FileDiffs fileDiffs) {
        super(fileDiffs, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
        this.fileDiffs = fileDiffs;
    }    

    public void doCompare(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        Field field = new Field(fileDiffs);
        field.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        field.compare(a, b);
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
