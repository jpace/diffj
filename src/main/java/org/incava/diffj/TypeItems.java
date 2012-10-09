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

    public void compare(ASTClassOrInterfaceDeclaration aNode, ASTClassOrInterfaceDeclaration bNode) {
        List<Type> amds = getDeclarationsOfClassType(aNode);
        List<Type> bmds = getDeclarationsOfClassType(bNode);

        TypeMatches<Type> matches = getTypeMatches(amds, bmds);

        List<Type> unprocA = new ArrayList<Type>(amds);        
        List<Type> unprocB = new ArrayList<Type>(bmds);

        compareMatches(matches, unprocA, unprocB);

        addRemoved(unprocA, bNode);        
        addAdded(aNode, unprocB);
    }

    public abstract String getName(Type t);

    public abstract String getAddedMessage(Type t);

    public abstract String getRemovedMessage(Type t);

    public abstract double getScore(Type amd, Type bmd);

    public abstract void doCompare(Type amd, Type bmd);

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

    public TypeMatches<Type> getTypeMatches(List<Type> amds, List<Type> bmds) {
        TypeMatches<Type> matches = new TypeMatches<Type>();

        for (Type amd : amds) {
            for (Type bmd : bmds) {
                double score = getScore(amd, bmd);
                if (score > 0.0) {
                    matches.add(score, amd, bmd);
                }
            }
        }
        return matches;
    }

    public void compareMatches(TypeMatches<Type> matches, List<Type> unprocA, List<Type> unprocB) {
        List<Double> descendingScores = matches.getDescendingScores();
        
        for (Double score : descendingScores) {
            // don't repeat comparisons ...

            List<Type> procA = new ArrayList<Type>();
            List<Type> procB = new ArrayList<Type>();

            for (Pair<Type, Type> declPair : matches.get(score)) {
                Type amd = declPair.getFirst();
                Type bmd = declPair.getSecond();

                if (unprocA.contains(amd) && unprocB.contains(bmd)) {
                    doCompare(amd, bmd);
                    
                    procA.add(amd);
                    procB.add(bmd);
                }
            }

            unprocA.removeAll(procA);
            unprocB.removeAll(procB);
        }
    }

    public List<Type> getDeclarationsOfClassType(ASTClassOrInterfaceDeclaration coid) {
        List<ASTClassOrInterfaceBodyDeclaration> decls = TypeDeclarationUtil.getDeclarations(coid);
        return getDeclarationsOfClass(decls);
    }

    public void addAdded(ASTClassOrInterfaceDeclaration aNode, List<Type> bs) {
        for (Type b : bs) {
            String name = getName(b);
            differences.added(aNode, b, getAddedMessage(b), name);
        }
    }

    public void addRemoved(List<Type> as, ASTClassOrInterfaceDeclaration bNode) {
        for (Type a : as) {
            String name = getName(a);
            differences.deleted(a, bNode, getRemovedMessage(a), name);
        }
    }
}
