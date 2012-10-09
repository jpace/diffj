package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * Compares super (extends or implements).
 */
public abstract class Supers {
    private final ASTClassOrInterfaceDeclaration decl;

    public Supers(ASTClassOrInterfaceDeclaration decl) {
        this.decl = decl;
    }

    abstract protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid);    
    
    abstract protected void superTypeChanged(ASTClassOrInterfaceType a, String aName, ASTClassOrInterfaceType b, String bName, Differences differences);

    abstract protected void superTypeAdded(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceType bType, String typeName, Differences differences);

    abstract protected void superTypeRemoved(ASTClassOrInterfaceType aType, ASTClassOrInterfaceDeclaration bt, String typeName, Differences differences);

    protected <K, V> K getFirstKey(Map<K, V> map) {
        return map.keySet().iterator().next();
    }
    
    public void diff(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        ASTClassOrInterfaceDeclaration fromDecl = decl;
        
        Map<String, ASTClassOrInterfaceType> fromMap = getMap(fromDecl);
        Map<String, ASTClassOrInterfaceType> toMap = getMap(toDecl);

        // I don't like this special case, but it is better than two separate
        // "add" and "remove" messages.

        if (fromMap.size() == 1 && toMap.size() == 1) {
            String fromName = getFirstKey(fromMap);
            String toName = getFirstKey(toMap);

            if (!fromName.equals(toName)) {
                ASTClassOrInterfaceType fromType = fromMap.get(fromName);
                ASTClassOrInterfaceType toType = toMap.get(toName);
                
                superTypeChanged(fromType, fromName, toType, toName, differences);
            }
        }
        else {
            List<String> typeNames = new ArrayList<String>();
            typeNames.addAll(fromMap.keySet());
            typeNames.addAll(toMap.keySet());

            // tr.Ace.log("typeNames", typeNames);

            for (String typeName : typeNames) {
                ASTClassOrInterfaceType fromType = fromMap.get(typeName);
                ASTClassOrInterfaceType toType = toMap.get(typeName);

                if (fromType == null) {
                    superTypeAdded(fromDecl, toType, typeName, differences);
                }
                else if (toType == null) {
                    superTypeRemoved(fromType, toDecl, typeName, differences);
                }
            }
        }
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid, String extImpClassName) {
        Map<String, ASTClassOrInterfaceType> map = new HashMap<String, ASTClassOrInterfaceType>();
        SimpleNode list = SimpleNodeUtil.findChild(coid, extImpClassName);

        if (list == null) {
            return map;
        }

        Collection<ASTClassOrInterfaceType> types = new ArrayList<ASTClassOrInterfaceType>();
        SimpleNodeUtil.fetchChildren(types, list, "net.sourceforge.pmd.ast.ASTClassOrInterfaceType");
        for (ASTClassOrInterfaceType type : types) {
            map.put(SimpleNodeUtil.toString(type), type);
        }
        
        return map;
    }
}
