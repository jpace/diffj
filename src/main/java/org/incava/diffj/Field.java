package org.incava.diffj;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.FieldUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Field extends Item {
    private final ASTFieldDeclaration field;

    public Field(ASTFieldDeclaration field) {
        this.field = field;
    }

    public void diff(ASTFieldDeclaration toFieldDecl, Differences differences) {
        Field toField = new Field(toFieldDecl);
        compareAccess(SimpleNodeUtil.getParent(field), SimpleNodeUtil.getParent(toFieldDecl), differences);
        compareModifiers(toField, differences);
        compareVariables(toField, differences);
    }

    protected SimpleNode getParent() {
        return SimpleNodeUtil.getParent(field);
    }

    protected void compareModifiers(Field toField, Differences differences) {
        SimpleNode fromParent = getParent();
        SimpleNode toParent = toField.getParent();
        FieldModifiers mods = new FieldModifiers(fromParent);
        mods.diff(toParent, differences);
    }

    protected void compareInitCode(String fromName, ASTVariableInitializer fromInit, String toName, ASTVariableInitializer toInit, Differences differences) {
        List<Token> fromCode = SimpleNodeUtil.getChildTokens(fromInit);
        List<Token> toCode = SimpleNodeUtil.getChildTokens(toInit);
        
        // It is logically impossible for this to execute where "to"
        // represents the from-file, and "from" the to-file, since "from.name"
        // would have matched "to.name" in the first loop of
        // compareVariableLists
        
        compareCode(fromName, fromCode, toCode, differences);
    }

    protected void compareVariableInits(ASTVariableDeclarator fromVar, ASTVariableDeclarator toVar, Differences differences) {
        ASTVariableInitializer fromInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(fromVar, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        ASTVariableInitializer toInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(toVar, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        
        if (fromInit == null) {
            if (toInit != null) {
                differences.changed(fromVar, toInit, Messages.INITIALIZER_ADDED);
            }
        }
        else if (toInit == null) {
            differences.changed(fromInit, toVar, Messages.INITIALIZER_REMOVED);
        }
        else {
            String fromName = FieldUtil.getName(fromVar).image;
            String toName = FieldUtil.getName(toVar).image;
            compareInitCode(fromName, fromInit, toName, toInit, differences);
        }
    }

    protected static Map<String, ASTVariableDeclarator> makeVDMap(List<ASTVariableDeclarator> vds) {
        Map<String, ASTVariableDeclarator> namesToVD = new HashMap<String, ASTVariableDeclarator>();

        for (ASTVariableDeclarator vd : vds) {
            String name = FieldUtil.getName(vd).image;
            namesToVD.put(name, vd);
        }

        return namesToVD;
    }

    protected void compareVariableTypes(String name, 
                                        ASTVariableDeclarator fromVarDecl, 
                                        Field toField, ASTVariableDeclarator toVarDecl, 
                                        Differences differences) {
        ASTType fromType = getType();
        ASTType toType = toField.getType();

        String fromTypeStr = SimpleNodeUtil.toString(fromType);
        String toTypeStr = SimpleNodeUtil.toString(toType);

        if (!fromTypeStr.equals(toTypeStr)) {
            differences.changed(fromType, toType, Messages.VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
        }

        compareVariableInits(fromVarDecl, toVarDecl, differences);
    }

    protected void processChangedVariable(ASTVariableDeclarator fromVarDecl, ASTVariableDeclarator toVarDecl, Differences differences) {
        Token fromTk = FieldUtil.getName(fromVarDecl);
        Token toTk = FieldUtil.getName(toVarDecl);
        differences.changed(fromTk, toTk, Messages.VARIABLE_CHANGED);
        compareVariableInits(fromVarDecl, toVarDecl, differences);
    }

    protected void processAddDelVariable(String name, String msg, ASTVariableDeclarator fromVarDecl, ASTVariableDeclarator toVarDecl, Differences differences) {
        Token fromTk = FieldUtil.getName(fromVarDecl);
        Token toTk = FieldUtil.getName(toVarDecl);
        differences.changed(fromTk, toTk, msg, name);
    }

    protected ASTType getType() {
        return (ASTType)SimpleNodeUtil.findChild(field, "net.sourceforge.pmd.ast.ASTType");
    }

    protected List<ASTVariableDeclarator> getVariables() {
        return SimpleNodeUtil.snatchChildren(field, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
    }

    protected void compareVariables(Field toField, Differences differences) {
        List<ASTVariableDeclarator> fromVarDecls = getVariables();
        List<ASTVariableDeclarator> toVarDecls = toField.getVariables();

        Map<String, ASTVariableDeclarator> fromNamesToVD = makeVDMap(fromVarDecls);
        Map<String, ASTVariableDeclarator> toNamesToVD = makeVDMap(toVarDecls);

        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToVD.keySet());
        names.addAll(toNamesToVD.keySet());

        for (String name : names) {
            ASTVariableDeclarator fromVarDecl = fromNamesToVD.get(name);
            ASTVariableDeclarator toVarDecl = toNamesToVD.get(name);

            if (fromVarDecl != null && toVarDecl != null) {
                compareVariableTypes(name, fromVarDecl, toField, toVarDecl, differences);
            }
            else if (fromVarDecls.size() == 1 && toVarDecls.size() == 1) {
                processChangedVariable(fromVarDecls.get(0), toVarDecls.get(0), differences);
            }
            else if (fromVarDecl == null) {
                processAddDelVariable(name, Messages.VARIABLE_ADDED, fromVarDecls.get(0), toVarDecl, differences);
            }
            else {
                processAddDelVariable(name, Messages.VARIABLE_REMOVED, fromVarDecl, toVarDecls.get(0), differences);
            }
        }
    }
}
