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

    public void compare(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        compareModifiers(a, b);
        compareVariables(a, b);
    }

    protected void compareModifiers(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        compareModifiers((SimpleNode)a.jjtGetParent(), (SimpleNode)b.jjtGetParent(), VALID_MODIFIERS);
    }

    protected void compareVariables(ASTVariableDeclarator a, ASTVariableDeclarator b) {
        ASTVariableInitializer ainit = (ASTVariableInitializer)SimpleNodeUtil.findChild(a, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        ASTVariableInitializer binit = (ASTVariableInitializer)SimpleNodeUtil.findChild(b, "net.sourceforge.pmd.ast.ASTVariableInitializer");
        
        if (ainit == null) {
            if (binit != null) {
                changed(a, binit, INITIALIZER_ADDED);
            }
        }
        else if (binit == null) {
            changed(ainit, b, INITIALIZER_REMOVED);
        }
        else {
            List<Token> aCode = SimpleNodeUtil.getChildrenSerially(ainit);
            List<Token> bCode = SimpleNodeUtil.getChildrenSerially(binit);

            // It is logically impossible for this to execute where "b"
            // represents the from-file, and "a" the to-file, since "a.name"
            // would have matched "b.name" in the first loop of
            // compareVariableLists

            String aName = FieldUtil.getName(a).image;
            String bName = FieldUtil.getName(b).image;
            
            compareCode(aName, aCode, bName, bCode);
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

    protected void compareVariables(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        ASTType aType = (ASTType)SimpleNodeUtil.findChild(a, "net.sourceforge.pmd.ast.ASTType");
        ASTType bType = (ASTType)SimpleNodeUtil.findChild(b, "net.sourceforge.pmd.ast.ASTType");        

        List<ASTVariableDeclarator> avds = SimpleNodeUtil.snatchChildren(a, "net.sourceforge.pmd.ast.ASTVariableDeclarator");
        List<ASTVariableDeclarator> bvds = SimpleNodeUtil.snatchChildren(b, "net.sourceforge.pmd.ast.ASTVariableDeclarator");

        Map<String, ASTVariableDeclarator> aNamesToVD = makeVDMap(avds);
        Map<String, ASTVariableDeclarator> bNamesToVD = makeVDMap(bvds);

        Collection<String> names = new TreeSet<String>();
        names.addAll(aNamesToVD.keySet());
        names.addAll(bNamesToVD.keySet());

        for (String name : names) {
            ASTVariableDeclarator avd = aNamesToVD.get(name);
            ASTVariableDeclarator bvd = bNamesToVD.get(name);

            if (avd == null || bvd == null) {
                if (avds.size() == 1 && bvds.size() == 1) {
                    Token aTk = FieldUtil.getName(avds.get(0));
                    Token bTk = FieldUtil.getName(bvds.get(0));
                    changed(aTk, bTk, VARIABLE_CHANGED);
                    compareVariables(avds.get(0), bvds.get(0));
                }
                else if (avd == null) {
                    Token aTk = FieldUtil.getName(avds.get(0));
                    Token bTk = FieldUtil.getName(bvd);
                    changed(aTk, bTk, VARIABLE_ADDED, name);
                }
                else {
                    Token aTk = FieldUtil.getName(avd);
                    Token bTk = FieldUtil.getName(bvds.get(0));
                    changed(aTk, bTk, VARIABLE_REMOVED, name);
                }
            }
            else {
                // types changed?

                String aTypeStr = SimpleNodeUtil.toString(aType);
                String bTypeStr = SimpleNodeUtil.toString(bType);

                if (!aTypeStr.equals(bTypeStr)) {
                    changed(aType, bType, VARIABLE_TYPE_CHANGED, name, aTypeStr, bTypeStr);
                }

                compareVariables(avd, bvd);
            }
        }
    }
}
