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

public class FieldDiff extends ItemDiff {
    public static final String VARIABLE_REMOVED = "variable removed: {0}";
    public static final String VARIABLE_ADDED = "variable added: {0}";
    public static final String VARIABLE_CHANGED = "variable changed from {0} to {1}";
    public static final String VARIABLE_TYPE_CHANGED = "variable type for {0} changed from {1} to {2}";
    public static final String INITIALIZER_REMOVED = "initializer removed";
    public static final String INITIALIZER_ADDED = "initializer added";

    protected static final int[] VALID_MODIFIERS = new int[] {
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC,
    };

    public FieldDiff(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        compareModifiers(from, to);
        compareVariables(from, to);
    }

    protected void compareModifiers(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        compareModifiers((SimpleNode)from.jjtGetParent(), (SimpleNode)to.jjtGetParent(), VALID_MODIFIERS);
    }

    protected void compareVariables(ASTVariableDeclarator from, ASTVariableDeclarator to) {
        ASTVariableInitializer fromInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(from, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        ASTVariableInitializer toInit = (ASTVariableInitializer)SimpleNodeUtil.findChild(to, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        
        if (fromInit == null) {
            if (toInit != null) {
                changed(from, toInit, INITIALIZER_ADDED);
            }
        }
        else if (toInit == null) {
            changed(fromInit, to, INITIALIZER_REMOVED);
        }
        else {
            List<Token> aCode = SimpleNodeUtil.getChildrenSerially(fromInit);
            List<Token> bCode = SimpleNodeUtil.getChildrenSerially(toInit);

            // It is logically impossible for this to execute where "to"
            // represents the from-file, and "from" the to-file, since "from.name"
            // would have matched "to.name" in the first loop of
            // compareVariableLists

            String fromName = FieldUtil.getName(from).image;
            String toName = FieldUtil.getName(to).image;
            
            compareCode(fromName, aCode, toName, bCode);
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

    protected void compareVariables(ASTFieldDeclaration from, ASTFieldDeclaration to) {
        ASTType fromType = (ASTType)SimpleNodeUtil.findChild(from, "net.sourceforge.pmd.ast.ASTType");
        ASTType toType = (ASTType)SimpleNodeUtil.findChild(to, "net.sourceforge.pmd.ast.ASTType");        

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

            if (fromVarDecl == null || toVarDecl == null) {
                if (fromVarDecls.size() == 1 && toVarDecls.size() == 1) {
                    Token fromTk = FieldUtil.getName(fromVarDecls.get(0));
                    Token toTk = FieldUtil.getName(toVarDecls.get(0));
                    changed(fromTk, toTk, VARIABLE_CHANGED);
                    compareVariables(fromVarDecls.get(0), toVarDecls.get(0));
                }
                else if (fromVarDecl == null) {
                    Token fromTk = FieldUtil.getName(fromVarDecls.get(0));
                    Token toTk = FieldUtil.getName(toVarDecl);
                    changed(fromTk, toTk, VARIABLE_ADDED, name);
                }
                else {
                    Token fromTk = FieldUtil.getName(fromVarDecl);
                    Token toTk = FieldUtil.getName(toVarDecls.get(0));
                    changed(fromTk, toTk, VARIABLE_REMOVED, name);
                }
            }
            else {
                // types changed?

                String fromTypeStr = SimpleNodeUtil.toString(fromType);
                String toTypeStr = SimpleNodeUtil.toString(toType);

                if (!fromTypeStr.equals(toTypeStr)) {
                    changed(fromType, toType, VARIABLE_TYPE_CHANGED, name, fromTypeStr, toTypeStr);
                }

                compareVariables(fromVarDecl, toVarDecl);
            }
        }
    }
}
