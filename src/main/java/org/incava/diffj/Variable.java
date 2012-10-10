package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * A list of variables within a type, such as:
 *
 * <pre>
 *     String s = "foo", t = "bar", u = null;
 * </pre>
 */
public class Variable extends Item {
    private final ASTType type;
    private final ASTVariableDeclarator variable;

    public Variable(ASTType type, ASTVariableDeclarator variable) {
        this.type = type;
        this.variable = variable;
    }

    public void diff(Variable toVariable, Differences differences) {
        String fromTypeStr = getTypeName();
        String toTypeStr = toVariable.getTypeName();

        if (!fromTypeStr.equals(toTypeStr)) {
            String name = getName();
            differences.changed(type, toVariable.type, Messages.VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
        }

        compareVariableInits(toVariable, differences);
    }

    public String getName() {
        return FieldUtil.getName(variable).image;
    }

    public ASTVariableInitializer getInitializer() {
        return (ASTVariableInitializer)SimpleNodeUtil.findChild(variable, "net.sourceforge.pmd.ast.ASTVariableInitializer");
    }

    public List<Token> getInitializerCode() {
        ASTVariableInitializer init = getInitializer();
        return SimpleNodeUtil.getChildTokens(init);
    }
    
    public String getTypeName() {
        return SimpleNodeUtil.toString(type);
    }

    protected void compareInitCode(Variable toVariable, Differences differences) {
        String fromName = getName();
        String toName = toVariable.getName();

        List<Token> fromCode = getInitializerCode();
        List<Token> toCode = toVariable.getInitializerCode();
        
        // It is logically impossible for this to execute where "to"
        // represents the from-file, and "from" the to-file, since "from.name"
        // would have matched "to.name" in the first loop of
        // compareVariableLists
        
        compareCode(fromName, fromCode, toCode, differences);
    }

    protected void compareVariableInits(Variable toVariable, Differences differences) {
        ASTVariableInitializer fromInit = getInitializer();
        ASTVariableInitializer toInit = toVariable.getInitializer();
        
        if (fromInit == null) {
            if (toInit != null) {
                differences.changed(variable, toInit, Messages.INITIALIZER_ADDED);
            }
        }
        else if (toInit == null) {
            differences.changed(fromInit, toVariable.variable, Messages.INITIALIZER_REMOVED);
        }
        else {
            compareInitCode(toVariable, differences);
        }
    }
}
