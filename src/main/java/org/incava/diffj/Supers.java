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
    protected final DiffComparator differences;

    public Supers(FileDiffs fileDiffs) {
        this.differences = new DiffComparator(fileDiffs);
    }

    abstract protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid);    
    
    abstract protected void superTypeChanged(ASTClassOrInterfaceType a, String aName, ASTClassOrInterfaceType b, String bName);

    abstract protected void superTypeAdded(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceType bType, String typeName);

    abstract protected void superTypeRemoved(ASTClassOrInterfaceType aType, ASTClassOrInterfaceDeclaration bt, String typeName);

    protected <K, V> K getFirstKey(Map<K, V> map) {
        return map.keySet().iterator().next();
    }
    
    protected void compare(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        Map<String, ASTClassOrInterfaceType> aMap = getMap(at);
        Map<String, ASTClassOrInterfaceType> bMap = getMap(bt);

        // I don't like this special case, but it is better than two separate
        // "add" and "remove" messages.

        if (aMap.size() == 1 && bMap.size() == 1) {
            String aName = getFirstKey(aMap);
            String bName = getFirstKey(bMap);

            if (!aName.equals(bName)) {
                ASTClassOrInterfaceType a = aMap.get(aName);
                ASTClassOrInterfaceType b = bMap.get(bName);
                
                superTypeChanged(a, aName, b, bName);
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
                    superTypeAdded(at, bType, typeName);
                }
                else if (bType == null) {
                    superTypeRemoved(aType, bt, typeName);
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
