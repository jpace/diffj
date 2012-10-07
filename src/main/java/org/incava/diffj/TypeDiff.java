package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTExtendsList;
import net.sourceforge.pmd.ast.ASTImplementsList;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class TypeDiff extends ItemDiff {
    public static final int[] VALID_TYPE_MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC, // valid only for inner types
        JavaParserConstants.STRICTFP
    };
    
    public TypeDiff(Report report) {
        super(report);
    }

    public TypeDiff(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTTypeDeclaration a, ASTTypeDeclaration b) {
        // should have only one child, the type itself, either an interface or a
        // class declaration

        ASTClassOrInterfaceDeclaration at = TypeDeclarationUtil.getType(a);
        ASTClassOrInterfaceDeclaration bt = TypeDeclarationUtil.getType(b);

        if (at == null && bt == null) {
            tr.Ace.log("skipping 'semicolon declarations'");
        }
        else {
            compare(at, bt);
        }
    }

    public void compare(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        if (!at.isInterface() && bt.isInterface()) {
            changed(at, bt, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (at.isInterface() && !bt.isInterface()) {
            changed(at, bt, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode atParent = SimpleNodeUtil.getParent(at);
        SimpleNode btParent = SimpleNodeUtil.getParent(bt);

        compareAccess(atParent, btParent);

        compareModifiers(atParent, btParent, VALID_TYPE_MODIFIERS);

        compareExtends(at, bt);
        compareImplements(at, bt);

        compareDeclarations(at, bt);
    }

    protected Map<String, ASTClassOrInterfaceType> getExtImpMap(ASTClassOrInterfaceDeclaration coid, String extImpClassName) {
        Map<String, ASTClassOrInterfaceType> map = new HashMap<String, ASTClassOrInterfaceType>();
        SimpleNode list = SimpleNodeUtil.findChild(coid, extImpClassName);

        if (list != null) {
            Collection<ASTClassOrInterfaceType> types = new ArrayList<ASTClassOrInterfaceType>();
            SimpleNodeUtil.fetchChildren(types, list, "net.sourceforge.pmd.ast.ASTClassOrInterfaceType");
            for (ASTClassOrInterfaceType type : types) {
                map.put(SimpleNodeUtil.toString(type), type);
            }
        }
        
        return map;
    }

    protected void compareImpExt(ASTClassOrInterfaceDeclaration at, 
                                 ASTClassOrInterfaceDeclaration bt, 
                                 String addMsg,
                                 String chgMsg,
                                 String delMsg,
                                 String extImpClsName) {
        Map<String, ASTClassOrInterfaceType> aMap = getExtImpMap(at, extImpClsName);
        Map<String, ASTClassOrInterfaceType> bMap = getExtImpMap(bt, extImpClsName);

        // I don't like this special case, but it is better than two separate
        // "add" and "remove" messages.

        if (aMap.size() == 1 && bMap.size() == 1) {
            String aName = aMap.keySet().iterator().next();
            String bName = bMap.keySet().iterator().next();

            if (!aName.equals(bName)) {
                ASTClassOrInterfaceType a = aMap.get(aName);
                ASTClassOrInterfaceType b = bMap.get(bName);
                
                changed(a, b, chgMsg, aName, bName);
            }
        }
        else {
            List<String> typeNames = new ArrayList<String>();
            typeNames.addAll(aMap.keySet());
            typeNames.addAll(bMap.keySet());

            // tr.Ace.log("typeNames", typeNames);

            for (String typeName : typeNames) {
                ASTClassOrInterfaceType aType = aMap.get(typeName);
                ASTClassOrInterfaceType bType = bMap.get(typeName);

                if (aType == null) {
                    changed(at, bType, addMsg, typeName);
                }
                else if (bType == null) {
                    changed(aType, bt, delMsg, typeName);
                }
            }
        }
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        compareImpExt(at, bt, Messages.EXTENDED_TYPE_ADDED, Messages.EXTENDED_TYPE_CHANGED, Messages.EXTENDED_TYPE_REMOVED, "net.sourceforge.pmd.ast.ASTExtendsList");
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        compareImpExt(at, bt, Messages.IMPLEMENTED_TYPE_ADDED, Messages.IMPLEMENTED_TYPE_CHANGED, Messages.IMPLEMENTED_TYPE_REMOVED, "net.sourceforge.pmd.ast.ASTImplementsList");
    }

    protected void compareDeclarations(ASTClassOrInterfaceDeclaration aNode, ASTClassOrInterfaceDeclaration bNode) {
        FileDiffs diffs = getFileDiffs();
        
        TypeMethodDiff tmd = new TypeMethodDiff(diffs);
        tmd.compare(aNode, bNode);
        
        TypeFieldDiff tfd = new TypeFieldDiff(diffs);
        tfd.compare(aNode, bNode);
        
        TypeCtorDiff ctd = new TypeCtorDiff(diffs);
        ctd.compare(aNode, bNode);
        
        TypeInnerTypeDiff titd = new TypeInnerTypeDiff(diffs, this);
        titd.compare(aNode, bNode);
    }

}
