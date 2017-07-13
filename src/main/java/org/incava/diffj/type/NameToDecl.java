package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.pmdx.Node;
import org.incava.pmdx.SimpleNodeUtil;

/**
 * The list of supertypes for 'extends' and 'implements'.
 */
public class NameToDecl {
    private final Map<String, ASTClassOrInterfaceType> map;

    public NameToDecl(ASTClassOrInterfaceDeclaration decl, Class<? extends AbstractJavaNode> extImpClass) {
        this.map = new HashMap<String, ASTClassOrInterfaceType>();
        AbstractJavaNode list = Node.of(decl).findChild(extImpClass);
        
        if (list == null) {
            return;
        }
        
        Collection<ASTClassOrInterfaceType> types = Node.of(list).findChildren(ASTClassOrInterfaceType.class);
        for (ASTClassOrInterfaceType type : types) {
            map.put(Node.of(type).toString(), type);
        }
    }
    
    public boolean hasOne() {
        return map.size() == 1;
    }

    public ASTClassOrInterfaceType get(String name) {
        return map.get(name);
    }

    public Collection<String> getNames() {
        return new TreeSet<String>(map.keySet());
    }

    protected String getFirstName() {
        return map.keySet().iterator().next();
    }
}
