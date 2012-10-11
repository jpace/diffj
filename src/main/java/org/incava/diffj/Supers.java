package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * Compares super (extends or implements).
 */
public abstract class Supers {
    private final ASTClassOrInterfaceDeclaration decl;

    public Supers(ASTClassOrInterfaceDeclaration decl) {
        this.decl = decl;
    }

    protected void superTypeAdded(ASTClassOrInterfaceType toType, String typeName, Differences differences) {
        differences.changed(decl, toType, getAddedMessage(), typeName);
    }

    protected void superTypeChanged(ASTClassOrInterfaceType fromType, String fromName, ASTClassOrInterfaceType toType, String toName, Differences differences) {
        differences.changed(fromType, toType, getChangedMessage(), fromName, toName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType fromType, String typeName, Differences differences) {
        differences.changed(fromType, decl, getRemovedMessage(), typeName);
    }

    abstract protected String getAstClassName();

    abstract protected String getAddedMessage();

    abstract protected String getChangedMessage();

    abstract protected String getRemovedMessage();

    protected <K, V> K getFirstKey(Map<K, V> map) {
        return map.keySet().iterator().next();
    }
    
    public void diff(Supers toSupers, Differences differences) {
        ASTClassOrInterfaceDeclaration toDecl = toSupers.decl;

        Map<String, ASTClassOrInterfaceType> fromMap = getMap();
        Map<String, ASTClassOrInterfaceType> toMap = toSupers.getMap();

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
                    superTypeAdded(toType, typeName, differences);
                }
                else if (toType == null) {
                    toSupers.superTypeRemoved(fromType, typeName, differences);
                }
            }
        }
    }

    protected Map<String, ASTClassOrInterfaceType> getMap() {
        String extImpClassName = getAstClassName();
        Map<String, ASTClassOrInterfaceType> map = new HashMap<String, ASTClassOrInterfaceType>();
        SimpleNode list = SimpleNodeUtil.findChild(decl, extImpClassName);

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
