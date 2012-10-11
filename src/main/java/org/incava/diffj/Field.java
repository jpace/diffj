package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;

public class Field extends Item {
    private final ASTFieldDeclaration field;

    public Field(ASTFieldDeclaration field) {
        super(field);
        this.field = field;
    }

    public void diff(Field toField, Differences differences) {
        compareAccess(toField, differences);
        compareModifiers(toField, differences);
        compareVariables(toField, differences);
    }

    protected FieldModifiers getModifiers() {
        return new FieldModifiers(getParent());
    }

    protected void compareModifiers(Field toField, Differences differences) {
        FieldModifiers fromMods = getModifiers();
        FieldModifiers toMods = toField.getModifiers();
        fromMods.diff(toMods, differences);
    }

    protected ASTType getType() {
        return (ASTType)SimpleNodeUtil.findChild(field, "net.sourceforge.pmd.ast.ASTType");
    }

    protected Variables getVariables() {
        List<ASTVariableDeclarator> varDecls = SimpleNodeUtil.snatchChildren(field, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
        return new Variables(getType(), varDecls);
    }

    protected void compareVariables(Field toField, Differences differences) {
        Variables fromVariables = getVariables();
        Variables toVariables = toField.getVariables();
        fromVariables.diff(toVariables, differences);
    }
}
