package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;

/**
 * Items represents the methods, ctors, fields and inner types of a parent type.
 * Collects PMD AST types into DiffJ Java types.
 */
public abstract class Items<DiffJType extends Diffable<DiffJType>, PmdAstType extends SimpleNode> {
    private final Class<PmdAstType> cls;
    private final Type type;

    public Items(ASTClassOrInterfaceDeclaration decl, Class<PmdAstType> cls) {
        this.type = new Type(decl);
        this.cls = cls;
    }

    public abstract DiffJType getAstType(PmdAstType item);

    public List<DiffJType> toAstTypeList(List<PmdAstType> its) {
        List<DiffJType> astList = new ArrayList<DiffJType>();
        for (PmdAstType it : its) {
            astList.add(getAstType(it));
        }
        return astList;
    }

    public List<DiffJType> getDeclarations() {
        List<PmdAstType> decls = type.getDeclarationsOfClass(cls);
        return toAstTypeList(decls);
    }

    public void diff(Items<DiffJType, PmdAstType> toItems, Differences differences) {
        List<DiffJType> fromTypes = getDeclarations();
        List<DiffJType> toTypes = toItems.getDeclarations();

        TypeMatches<DiffJType> matches = new TypeMatches<DiffJType>(fromTypes);
        matches.diff(toTypes, differences);

        List<DiffJType> removed = matches.getRemoved();
        List<DiffJType> added = matches.getAdded();

        addRemoved(removed, toItems.type, differences);
        addAdded(added, differences);
    }

    public void addAdded(List<DiffJType> added, Differences differences) {
        for (DiffJType toAdd : added) {
            String name = toAdd.getName();
            differences.added(type.getNode(), toAdd.getNode(), toAdd.getAddedMessage(), name);
        }
    }

    public void addRemoved(List<DiffJType> removed, Type toType, Differences differences) {
        for (DiffJType goner : removed) {
            String name = goner.getName();
            differences.deleted(goner.getNode(), toType.getNode(), goner.getRemovedMessage(), name);
        }
    }
}
