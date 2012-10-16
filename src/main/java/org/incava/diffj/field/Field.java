package org.incava.diffj.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.AccessibleElement;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.util.CollectionExt;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.VariableUtil;

public class Field extends AccessibleElement implements Diffable<Field> {
    public static final String FIELD_REMOVED = "field removed: {0}";
    public static final String FIELD_ADDED = "field added: {0}";    

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

    public String getName() {
        return FieldUtil.getNames(field);
    }

    public String getAddedMessage() {
        return FIELD_ADDED;
    }

    public String getRemovedMessage() {
        return FIELD_REMOVED;
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
        return SimpleNodeUtil.findChild(field, net.sourceforge.pmd.ast.ASTType.class);
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

    /**
     * Returns a list of strings of the names of the variables declared in this
     * field.
     */
    public List<String> getNameList() {
        List<ASTVariableDeclarator> varDecls = SimpleNodeUtil.snatchChildren(field, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
        List<String> names = new ArrayList<String>();
        for (ASTVariableDeclarator varDecl : varDecls) {
            names.add(VariableUtil.getName(varDecl).image);
        }
        return names;
    }

    public double getMatchScore(Field toField) {
        // a field can have more than one name.

        List<String> fromNames = getNameList();
        List<String> toNames = toField.getNameList();

        Set<String> inBoth = CollectionExt.intersection(fromNames, toNames);

        int     matched = inBoth.size();
        int     count   = Math.max(fromNames.size(), toNames.size());
        double  score   = 0.5 * matched / count;

        ASTType fromType = getType();
        ASTType toType   = toField.getType();

        if (toString(fromType).equals(toString(toType))) {
            score += 0.5;
        }
        
        return score;
    }

    /**
     * Returns the token images for the node.
     */
    public String toString(SimpleNode node) {
        Token tk = node.getFirstToken();
        Token last = node.getLastToken();
        StringBuilder sb = new StringBuilder(tk.image);
        while (tk != last) {
            tk = tk.next;
            sb.append(tk.image);
        }
        return sb.toString();
    }
}
