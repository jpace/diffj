package org.incava.diffj.field;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.FieldUtil;

/**
 * A list of variables within a type, such as:
 *
 * <pre>
 *     String s = "foo", t = "bar", u = null;
 * </pre>
 */
public class Variables {
    public static final Message VARIABLE_REMOVED = new Message("variable removed: {0}");
    public static final Message VARIABLE_ADDED = new Message("variable added: {0}");
    public static final Message VARIABLE_CHANGED = new Message("variable changed from {0} to {1}");

    private final ASTType type;
    private final List<ASTVariableDeclarator> variables;

    public Variables(ASTType type, List<ASTVariableDeclarator> variables) {
        this.type = type;
        this.variables = variables;
    }

    public void diff(Variables toVariables, Differences differences) {
        Map<String, ASTVariableDeclarator> fromNamesToDecls = getNamesToDeclarations();
        Map<String, ASTVariableDeclarator> toNamesToDecls = toVariables.getNamesToDeclarations();

        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToDecls.keySet());
        names.addAll(toNamesToDecls.keySet());

        for (String name : names) {
            ASTVariableDeclarator fromVarDecl = fromNamesToDecls.get(name);
            ASTVariableDeclarator toVarDecl = toNamesToDecls.get(name);

            if (fromVarDecl != null && toVarDecl != null) {
                Variable variable = new Variable(type, fromVarDecl);
                Variable toVariable = new Variable(toVariables.type, toVarDecl);
                variable.diff(toVariable, differences);
            }
            else if (fromVarDecl == null) {
                processAddDelVariable(name, VARIABLE_ADDED, variables.get(0), toVarDecl, differences);
            }
            else {
                processAddDelVariable(name, VARIABLE_REMOVED, fromVarDecl, toVariables.variables.get(0), differences);
            }
        }
    }

    protected Map<String, ASTVariableDeclarator> getNamesToDeclarations() {
        Map<String, ASTVariableDeclarator> namesToDecls = new HashMap<String, ASTVariableDeclarator>();

        for (ASTVariableDeclarator var : variables) {
            String name = FieldUtil.getName(var).image;
            namesToDecls.put(name, var);
        }

        return namesToDecls;
    }

    protected void processAddDelVariable(String name, Message msg, ASTVariableDeclarator fromVarDecl, ASTVariableDeclarator toVarDecl, Differences differences) {
        Token fromTk = FieldUtil.getName(fromVarDecl);
        Token toTk = FieldUtil.getName(toVarDecl);
        differences.changed(fromTk, toTk, msg, name);
    }
}
