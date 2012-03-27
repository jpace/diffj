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
    public static final String TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface";
    public static final String TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class";

    public static final String METHOD_REMOVED = "method removed: {0}";
    public static final String METHOD_ADDED = "method added: {0}";
    public static final String METHOD_CHANGED = "method changed from {0} to {1}";

    public static final String CONSTRUCTOR_REMOVED = "constructor removed: {0}";
    public static final String CONSTRUCTOR_ADDED = "constructor added: {0}";

    public static final String FIELD_REMOVED = "field removed: {0}";
    public static final String FIELD_ADDED = "field added: {0}";

    public static final String INNER_INTERFACE_ADDED = "inner interface added: {0}";
    public static final String INNER_INTERFACE_REMOVED = "inner interface removed: {0}";

    public static final String INNER_CLASS_ADDED = "inner class added: {0}";
    public static final String INNER_CLASS_REMOVED = "inner class removed: {0}";

    public static final String EXTENDED_TYPE_REMOVED = "extended type removed: {0}";
    public static final String EXTENDED_TYPE_ADDED = "extended type added: {0}";
    public static final String EXTENDED_TYPE_CHANGED = "extended type changed from {0} to {1}";

    public static final String IMPLEMENTED_TYPE_REMOVED = "implemented type removed: {0}";
    public static final String IMPLEMENTED_TYPE_ADDED = "implemented type added: {0}";
    public static final String IMPLEMENTED_TYPE_CHANGED = "implemented type changed from {0} to {1}";

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

    public void compare(SimpleNode aType, SimpleNode bType) {
        // should have only one child, the type itself, either an interface or a
        // class declaration

        ASTTypeDeclaration a = (ASTTypeDeclaration)aType;
        ASTTypeDeclaration b = (ASTTypeDeclaration)bType;

        compare(a, b);
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
        tr.Ace.log("at", at);
        tr.Ace.log("bt", bt);

        if (!at.isInterface() && bt.isInterface()) {
            changed(at, bt, TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (at.isInterface() && !bt.isInterface()) {
            changed(at, bt, TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode atParent = SimpleNodeUtil.getParent(at);
        SimpleNode btParent = SimpleNodeUtil.getParent(bt);

        compareAccess(atParent, btParent);

        compareModifiers(atParent, btParent, VALID_TYPE_MODIFIERS);

        compareExtends(at, bt);
        compareImplements(at, bt);

        compareDeclarations(at, bt);
    }

    protected Map<String, ASTClassOrInterfaceType> getExtImpMap(ASTClassOrInterfaceDeclaration coid, Class extImpClass) {
        Map<String, ASTClassOrInterfaceType> map = new HashMap<String, ASTClassOrInterfaceType>();
        SimpleNode list = SimpleNodeUtil.findChild(coid, extImpClass);

        if (list != null) {
            ASTClassOrInterfaceType[] types = (ASTClassOrInterfaceType[])SimpleNodeUtil.findChildren(list, ASTClassOrInterfaceType.class);
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
                                 Class extImpCls) {
        Map<String, ASTClassOrInterfaceType> aMap = getExtImpMap(at, extImpCls);
        Map<String, ASTClassOrInterfaceType> bMap = getExtImpMap(bt, extImpCls);

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
        compareImpExt(at, bt, EXTENDED_TYPE_ADDED, EXTENDED_TYPE_CHANGED, EXTENDED_TYPE_REMOVED, ASTExtendsList.class);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        compareImpExt(at, bt, IMPLEMENTED_TYPE_ADDED, IMPLEMENTED_TYPE_CHANGED, IMPLEMENTED_TYPE_REMOVED, ASTImplementsList.class);
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
