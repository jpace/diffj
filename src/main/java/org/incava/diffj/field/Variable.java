package org.incava.diffj.field;

import java.util.List;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Messages;
import org.incava.diffj.element.CodedElement;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * A list of variables within a type, such as:
 *
 * <pre>
 *     String s = "foo", t = "bar", u = null;
 * </pre>
 */
public class Variable extends CodedElement {
    public static final String INITIALIZER_REMOVED = "initializer removed";
    public static final String INITIALIZER_ADDED = "initializer added";
    public static final String VARIABLE_TYPE_CHANGED = "variable type for {0} changed from {1} to {2}";

    private final ASTType type;
    private final ASTVariableDeclarator variable;
    private final ASTVariableInitializer init;

    public Variable(ASTType type, ASTVariableDeclarator variable) {
        super(variable);
        
        this.type = type;
        this.variable = variable;
        this.init = SimpleNodeUtil.findChild(variable, net.sourceforge.pmd.ast.ASTVariableInitializer.class);
    }

    public void diff(Variable toVariable, Differences differences) {
        String fromTypeStr = getTypeName();
        String toTypeStr = toVariable.getTypeName();

        if (!fromTypeStr.equals(toTypeStr)) {
            String name = getName();
            differences.changed(type, toVariable.type, VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
        }

        compareVariableInits(toVariable, differences);
    }

    public String getName() {
        return FieldUtil.getName(variable).image;
    }

    public ASTVariableInitializer getInitializer() {
        return init;
    }

    public boolean hasInitializer() {
        return init != null;
    }

    public List<Token> getCodeTokens() {
        return SimpleNodeUtil.getChildTokens(init);
    }    
    
    public String getTypeName() {
        return SimpleNodeUtil.toString(type);
    }

    protected void compareVariableInits(Variable toVariable, Differences differences) {
        ASTVariableInitializer fromInit = getInitializer();
        ASTVariableInitializer toInit = toVariable.getInitializer();

        if (hasInitializer()) {
            if (toVariable.hasInitializer()) {
                compareCode(toVariable, differences);
            }
            else {
                differences.changed(init, toVariable.getNode(), INITIALIZER_REMOVED);
            }
        }
        else if (toVariable.hasInitializer()) {
            differences.changed(variable, toVariable.getInitializer(), INITIALIZER_ADDED);
        }
    }
}
