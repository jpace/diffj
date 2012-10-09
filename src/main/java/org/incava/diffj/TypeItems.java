package org.incava.diffj;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiffs;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;
import org.incava.pmdx.TypeDeclarationUtil;

public abstract class TypeItems<Type extends SimpleNode> {
    protected final Differences differences;
    private final String clsName;

    public TypeItems(FileDiffs fileDiffs, String clsName) {
        this.differences = new Differences(fileDiffs);
        this.clsName = clsName;
    }

    public TypeItems(FileDiffs differences, Class<Type> cls) {
        this(differences, cls == null ? (String)null : cls.getName());
    }

    public void compare(ASTClassOrInterfaceDeclaration fromType, ASTClassOrInterfaceDeclaration toType) {
        List<Type> fromDecls = getDeclarationsOfClassType(fromType);
        List<Type> toDecls = getDeclarationsOfClassType(toType);

        TypeMatches<Type> matches = getTypeMatches(fromDecls, toDecls);

        List<Type> unprocFromDecls = new ArrayList<Type>(fromDecls);        
        List<Type> unprocToDecls = new ArrayList<Type>(toDecls);

        compareMatches(matches, unprocFromDecls, unprocToDecls);

        addRemoved(unprocFromDecls, toType);        
        addAdded(fromType, unprocToDecls);
    }

    public abstract String getName(Type item);

    public abstract String getAddedMessage(Type item);

    public abstract String getRemovedMessage(Type item);

    public abstract double getScore(Type fromItem, Type toItem);

    public abstract void doCompare(Type fromItem, Type toItem);

    @SuppressWarnings("unchecked")
    public <Type extends SimpleNode> List<Type> getDeclarationsOfClass(List<ASTClassOrInterfaceBodyDeclaration> decls) {
        List<Type> declList = new ArrayList<Type>();

        for (ASTClassOrInterfaceBodyDeclaration decl : decls) {
            SimpleNode dec = TypeDeclarationUtil.getDeclaration(decl, clsName);

            if (dec != null) {
                declList.add((Type)dec);
            }   
        }
        
        return declList;
    }

    public TypeMatches<Type> getTypeMatches(List<Type> fromItems, List<Type> toItems) {
        TypeMatches<Type> matches = new TypeMatches<Type>();

        for (Type amd : fromItems) {
            for (Type bmd : toItems) {
                double score = getScore(amd, bmd);
                if (score > 0.0) {
                    matches.add(score, amd, bmd);
                }
            }
        }
        return matches;
    }

    public void compareMatches(TypeMatches<Type> matches, List<Type> unprocFromItems, List<Type> unprocToItems) {
        List<Double> descendingScores = matches.getDescendingScores();
        
        for (Double score : descendingScores) {
            // don't repeat comparisons ...

            List<Type> procFromItems = new ArrayList<Type>();
            List<Type> procToItems = new ArrayList<Type>();

            for (Pair<Type, Type> declPair : matches.get(score)) {
                Type fromItem = declPair.getFirst();
                Type toItem = declPair.getSecond();

                if (unprocFromItems.contains(fromItem) && unprocToItems.contains(toItem)) {
                    doCompare(fromItem, toItem);
                    
                    procFromItems.add(fromItem);
                    procToItems.add(toItem);
                }
            }

            unprocFromItems.removeAll(procFromItems);
            unprocToItems.removeAll(procToItems);
        }
    }

    public List<Type> getDeclarationsOfClassType(ASTClassOrInterfaceDeclaration coid) {
        List<ASTClassOrInterfaceBodyDeclaration> decls = TypeDeclarationUtil.getDeclarations(coid);
        return getDeclarationsOfClass(decls);
    }

    public void addAdded(ASTClassOrInterfaceDeclaration fromDecl, List<Type> toItems) {
        for (Type toItem : toItems) {
            String name = getName(toItem);
            differences.added(fromDecl, toItem, getAddedMessage(toItem), name);
        }
    }

    public void addRemoved(List<Type> fromItems, ASTClassOrInterfaceDeclaration toDecl) {
        for (Type fromItem : fromItems) {
            String name = getName(fromItem);
            differences.deleted(fromItem, toDecl, getRemovedMessage(fromItem), name);
        }
    }
}
