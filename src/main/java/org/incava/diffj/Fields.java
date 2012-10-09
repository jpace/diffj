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

public class Fields extends Items {
    public Fields(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        compareModifiers(from, to);
        compareVariables(from, to);
    }

    protected void compareModifiers(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        SimpleNode fromParent = SimpleNodeUtil.getParent(from);
        SimpleNode toParent = SimpleNodeUtil.getParent(to);
        MethodModifiers mods = new MethodModifiers(fromParent);
        mods.diff(toParent, differences);
    }

    protected void compareInitCode(String fromName, ASTVariableInitializer fromInit, String toName, ASTVariableInitializer toInit) {
        List<Token> aCode = SimpleNodeUtil.getChildTokens(fromInit);
        List<Token> bCode = SimpleNodeUtil.getChildTokens(toInit);
        
        // It is logically impossible for this to execute where "to"
        // represents the from-file, and "from" the to-file, since "from.name"
        // would have matched "to.name" in the first loop of
        // compareVariableLists
        
        compareCode(fromName, aCode, bCode);
    }

    protected void compareVariableInits(ASTVariableDeclarator from, ASTVariableDeclarator to) {
        ASTVariableInitializer fromInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(from, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        ASTVariableInitializer toInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(to, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        
        if (fromInit == null) {
            if (toInit != null) {
                differences.changed(from, toInit, Messages.INITIALIZER_ADDED);
            }
        }
        else if (toInit == null) {
            differences.changed(fromInit, to, Messages.INITIALIZER_REMOVED);
        }
        else {
            String fromName = FieldUtil.getName(from).image;
            String toName = FieldUtil.getName(to).image;
            compareInitCode(fromName, fromInit, toName, toInit);
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

    protected void compareVariableTypes(String name, ASTFieldDeclaration fromFieldDecl, ASTVariableDeclarator fromVarDecl, ASTFieldDeclaration toFieldDecl, ASTVariableDeclarator toVarDecl) {
        ASTType fromType = (ASTType)SimpleNodeUtil.findChild(fromFieldDecl, "net.sourceforge.pmd.ast.ASTType");
        ASTType toType = (ASTType)SimpleNodeUtil.findChild(toFieldDecl, "net.sourceforge.pmd.ast.ASTType");

        String fromTypeStr = SimpleNodeUtil.toString(fromType);
        String toTypeStr = SimpleNodeUtil.toString(toType);

        if (!fromTypeStr.equals(toTypeStr)) {
            differences.changed(fromType, toType, Messages.VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
        }

        compareVariableInits(fromVarDecl, toVarDecl);
    }

    protected void processChangedVariable(ASTVariableDeclarator fromVarDecl, ASTVariableDeclarator toVarDecl) {
        Token fromTk = FieldUtil.getName(fromVarDecl);
        Token toTk = FieldUtil.getName(toVarDecl);
        differences.changed(fromTk, toTk, Messages.VARIABLE_CHANGED);
        compareVariableInits(fromVarDecl, toVarDecl);
    }

    protected void processAddDelVariable(String name, String msg, ASTVariableDeclarator fromVarDecl, ASTVariableDeclarator toVarDecl) {
        Token fromTk = FieldUtil.getName(fromVarDecl);
        Token toTk = FieldUtil.getName(toVarDecl);
        differences.changed(fromTk, toTk, msg, name);
    }

    protected void compareVariables(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        List<ASTVariableDeclarator> fromVarDecls = SimpleNodeUtil.snatchChildren(from, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
        List<ASTVariableDeclarator> toVarDecls = SimpleNodeUtil.snatchChildren(to, "net.sourceforge.pmd.ast.ASTVariableDeclarator");

        Map<String, ASTVariableDeclarator> fromNamesToVD = makeVDMap(fromVarDecls);
        Map<String, ASTVariableDeclarator> toNamesToVD = makeVDMap(toVarDecls);

        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToVD.keySet());
        names.addAll(toNamesToVD.keySet());

        for (String name : names) {
            ASTVariableDeclarator fromVarDecl = fromNamesToVD.get(name);
            ASTVariableDeclarator toVarDecl = toNamesToVD.get(name);

            if (fromVarDecl != null && toVarDecl != null) {
                compareVariableTypes(name, from, fromVarDecl, to, toVarDecl);
            }
            else if (fromVarDecls.size() == 1 && toVarDecls.size() == 1) {
                processChangedVariable(fromVarDecls.get(0), toVarDecls.get(0));
            }
            else if (fromVarDecl == null) {
                processAddDelVariable(name, Messages.VARIABLE_ADDED, fromVarDecls.get(0), toVarDecl);
            }
            else {
                processAddDelVariable(name, Messages.VARIABLE_REMOVED, fromVarDecl, toVarDecls.get(0));
            }
        }
    }
}
