package org.incava.diffj;

import java.awt.Point;
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
import org.incava.analysis.FileDiff;
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

    public FieldDiff(Collection<FileDiff> differences) {
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
        ASTVariableInitializer ainit = (ASTVariableInitializer)SimpleNodeUtil.findChild(a, ASTVariableInitializer.class);
        ASTVariableInitializer binit = (ASTVariableInitializer)SimpleNodeUtil.findChild(b, ASTVariableInitializer.class);
        
        if (ainit == null) {
            if (binit == null) {
                // no change in init
            }
            else {
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

    protected static Map<String, ASTVariableDeclarator> makeVDMap(ASTVariableDeclarator[] vds) {
        Map<String, ASTVariableDeclarator> namesToVD = new HashMap<String, ASTVariableDeclarator>();

        for (int vi = 0; vi < vds.length; ++vi) {
            ASTVariableDeclarator vd = vds[vi];
            String name = FieldUtil.getName(vd).image;
            namesToVD.put(name, vd);
        }

        return namesToVD;
    }

    protected void compareVariables(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        ASTType aType = (ASTType)SimpleNodeUtil.findChild(a, ASTType.class);
        ASTType bType = (ASTType)SimpleNodeUtil.findChild(b, ASTType.class);        

        ASTVariableDeclarator[] avds = (ASTVariableDeclarator[])SimpleNodeUtil.findChildren(a, ASTVariableDeclarator.class);
        ASTVariableDeclarator[] bvds = (ASTVariableDeclarator[])SimpleNodeUtil.findChildren(b, ASTVariableDeclarator.class);

        Map<String, ASTVariableDeclarator> aNamesToVD = makeVDMap(avds);
        Map<String, ASTVariableDeclarator> bNamesToVD = makeVDMap(bvds);

        Collection<String> names = new TreeSet<String>();
        names.addAll(aNamesToVD.keySet());
        names.addAll(bNamesToVD.keySet());

        for (String name : names) {
            ASTVariableDeclarator avd = aNamesToVD.get(name);
            ASTVariableDeclarator bvd = bNamesToVD.get(name);

            if (avd == null || bvd == null) {
                if (avds.length == 1 && bvds.length == 1) {
                    Token aTk = FieldUtil.getName(avds[0]);
                    Token bTk = FieldUtil.getName(bvds[0]);
                    changed(aTk, bTk, VARIABLE_CHANGED);
                    compareVariables(avds[0], bvds[0]);
                }
                else if (avd == null) {
                    Token aTk = FieldUtil.getName(avds[0]);
                    Token bTk = FieldUtil.getName(bvd);
                    changed(aTk, bTk, VARIABLE_ADDED, name);
                }
                else {
                    Token aTk = FieldUtil.getName(avd);
                    Token bTk = FieldUtil.getName(bvds[0]);
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
