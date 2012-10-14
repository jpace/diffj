package org.incava.diffj;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

/**
 * Items represents the methods, ctors, fields and inner types of a parent type.
 */
public abstract class Items<ASTType extends Diffable<ASTType>, ItemType extends SimpleNode> {
    private final String clsName;
    private final Type type;

    public Items(ASTClassOrInterfaceDeclaration decl, String clsName) {
        this.type = new Type(decl);
        this.clsName = clsName;
    }

    public void diff(Items<ASTType, ItemType> toItems, Differences differences) {
    }

    public List<ASTType> toAstTypeList(List<ItemType> its) {
        List<ASTType> astList = new ArrayList<ASTType>();
        for (ItemType it : its) {
            astList.add(getAstType(it));
        }
        return astList;
    }

    public void diff(Type toType, Differences differences) {
        List<ItemType> fromDecls = type.getDeclarationsOfClassType(clsName);
        List<ItemType> toDecls = toType.getDeclarationsOfClassType(clsName);

        List<ASTType> fromTypes = toAstTypeList(fromDecls);
        List<ASTType> toTypes = toAstTypeList(toDecls);

        TypeMatches<ASTType, ItemType> matches = new TypeMatches<ASTType, ItemType>(this, fromDecls);
        matches.diff(toDecls, differences);

        List<ASTType> removed = matches.getRemoved();
        List<ASTType> added = matches.getAdded();

        addRemoved(removed, toType, differences);
        addAdded(added, differences);
    }

    public abstract ASTType getAstType(ItemType item);

    public void addAdded(List<ASTType> added, Differences differences) {
        for (ASTType toType : added) {
            tr.Ace.onMagenta("toType", toType);
            String name = toType.getName();
            differences.added(type.getDeclaration(), toType.getNode(), toType.getAddedMessage(), name);
        }
    }

    public void addRemoved(List<ASTType> removed, Type toType, Differences differences) {
        for (ASTType goner : removed) {
            tr.Ace.onRed("goner", goner);
            String name = goner.getName();
            differences.deleted(goner.getNode(), toType.getDeclaration(), goner.getRemovedMessage(), name);
        }
    }
}
