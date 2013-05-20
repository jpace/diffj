package org.incava.diffj.type;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;

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

    abstract protected Class<? extends SimpleNode> getPmdClass();

    abstract protected Message getAddedMessage();

    abstract protected Message getChangedMessage();

    abstract protected Message getRemovedMessage();
    
    public void diff(Supers toSupers, Differences differences) {
        ASTClassOrInterfaceDeclaration toDecl = toSupers.decl;

        NameToDecl fromMap = getMap();
        NameToDecl toMap = toSupers.getMap();

        if (fromMap.hasOne() && toMap.hasOne()) {
            // I don't like this special case, but it is better than two separate
            // "add" and "remove" messages.
            compareOne(fromMap, toMap, differences);
        }
        else {
            compareEach(fromMap, toMap, toSupers, differences);
        }
    }

    protected void compareOne(NameToDecl fromMap, NameToDecl toMap, Differences differences) {
        String fromName = fromMap.getFirstName();
        String toName = toMap.getFirstName();

        if (!fromName.equals(toName)) {
            ASTClassOrInterfaceType fromType = fromMap.get(fromName);
            ASTClassOrInterfaceType toType = toMap.get(toName);
                
            superTypeChanged(fromType, fromName, toType, toName, differences);
        }
    }

    protected void compareEach(NameToDecl fromMap, NameToDecl toMap, Supers toSupers, Differences differences) {
        Collection<String> names = fromMap.getNames();
        names.addAll(toMap.getNames());

        for (String name : names) {
            ASTClassOrInterfaceType fromType = fromMap.get(name);
            ASTClassOrInterfaceType toType = toMap.get(name);

            if (fromType == null) {
                superTypeAdded(toType, name, differences);
            }
            else if (toType == null) {
                toSupers.superTypeRemoved(fromType, name, differences);
            }
        }
    }

    protected NameToDecl getMap() {
        Class<? extends SimpleNode> pmdCls = getPmdClass();
        return new NameToDecl(decl, pmdCls);
    }
}
