package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;


/**
 * Miscellaneous routines for type declarations.
 */
public class TypeDeclarationUtil extends SimpleNodeUtil {

    public static Token getName(ASTTypeDeclaration typeDecl) {
        ASTClassOrInterfaceDeclaration cidecl = (ASTClassOrInterfaceDeclaration)findChild(typeDecl, ASTClassOrInterfaceDeclaration.class);
        return cidecl == null ? null : cidecl.getFirstToken().next;
    }

    public static ASTClassOrInterfaceDeclaration getType(ASTTypeDeclaration typeDecl) {
        return (ASTClassOrInterfaceDeclaration)SimpleNodeUtil.findChild(typeDecl, ASTClassOrInterfaceDeclaration.class);
    }

    public static ASTTypeDeclaration findTypeDeclaration(String name, ASTTypeDeclaration[] types) {
        for (int i = 0; i < types.length; ++i) {
            ASTTypeDeclaration type      = types[i];
            Token              otherName = getName(type);

            if ((otherName == null && name == null) ||
                (otherName != null && otherName.image.equals(name))) {
                return type;
            }
        }

        return null;
    }

    /**
     * Returns a list of all methods, fields, constructors, and inner classes
     * and interfaces.
     */
    public static ASTClassOrInterfaceBodyDeclaration[] getDeclarations(ASTTypeDeclaration tdecl) {
        ASTClassOrInterfaceDeclaration cidecl = (ASTClassOrInterfaceDeclaration)findChild(tdecl, ASTClassOrInterfaceDeclaration.class);
        return getDeclarations(cidecl);
    }

    /**
     * Returns a list of all methods, fields, constructors, and inner classes
     * and interfaces.
     */
    public static ASTClassOrInterfaceBodyDeclaration[] getDeclarations(ASTClassOrInterfaceDeclaration coid) {
        ASTClassOrInterfaceBody body = (ASTClassOrInterfaceBody)findChild(coid, ASTClassOrInterfaceBody.class);
        return (ASTClassOrInterfaceBodyDeclaration[])findChildren(body, ASTClassOrInterfaceBodyDeclaration.class);
    }

    /**
     * Returns the real declaration, which is a method, field, constructor, or
     * inner class or interface.
     */
    public static SimpleNode getDeclaration(ASTClassOrInterfaceBodyDeclaration bdecl) {
        return hasChildren(bdecl) ? findChild(bdecl, null) : null;
    }

    /**
     * Returns the real declaration, which is a method, field, constructor, or
     * inner class or interface.
     */
    public static SimpleNode getDeclaration(ASTClassOrInterfaceBodyDeclaration bdecl, Class<? extends SimpleNode> cls) {
        return hasChildren(bdecl) ? findChild(bdecl, cls) : null;
    }

    public static Map<Double, List<Pair<SimpleNode, SimpleNode>>> matchDeclarations(ASTClassOrInterfaceBodyDeclaration[] aDecls, 
                                                                                    ASTClassOrInterfaceBodyDeclaration[] bDecls, 
                                                                                    MethodUtil methodUtil) {
        
        // keys (scores) maintained in reversed order:
        TreeMap<Double, List<Pair<SimpleNode, SimpleNode>>> byScore = new TreeMap<Double, List<Pair<SimpleNode, SimpleNode>>>(new ReverseComparator<Double>());

        // map b by declaration types

        for (ASTClassOrInterfaceBodyDeclaration aDecl : aDecls) {
            for (ASTClassOrInterfaceBodyDeclaration bDecl : bDecls) {
                double score = getMatchScore(aDecl, bDecl, methodUtil);
                if (score > 0.0) {
                    Double dScore  = new Double(score);
                    List<Pair<SimpleNode, SimpleNode>> atScore = byScore.get(dScore);
                    if (atScore == null) {
                        atScore = new ArrayList<Pair<SimpleNode, SimpleNode>>();
                        byScore.put(dScore, atScore);
                    }
                    atScore.add(Pair.create((SimpleNode)aDecl, (SimpleNode)bDecl));
                }
            }
        }

        if (true) {
            return byScore;
        }

        // The nonsense below purges values with worse scores. But this also
        // means that it removes elements with the same scores.

        List<SimpleNode> aSeen  = new ArrayList<SimpleNode>();
        List<SimpleNode> bSeen  = new ArrayList<SimpleNode>();

        Iterator<Double> sit = byScore.keySet().iterator();

        while (sit.hasNext()) {
            Double             dScore  = sit.next();
            List<Pair<SimpleNode, SimpleNode>>     atScore = byScore.get(dScore);
            Iterator<Pair<SimpleNode, SimpleNode>> vit     = atScore.iterator();

            while (vit.hasNext()) {
                Pair<SimpleNode, SimpleNode>   values = vit.next();
                SimpleNode a      = values.getFirst();
                SimpleNode b      = values.getSecond();

                if (aSeen.contains(a)) {
                    // a already seen
                    vit.remove();
                }
                else if (bSeen.contains(b)) {
                    // b already seen
                    vit.remove();
                }
                else {                    
                    // neither already seen
                    aSeen.add(a);
                    bSeen.add(b);
                }
            }
            
            if (atScore.isEmpty()) {
                // remove the empty list
                sit.remove();
            }
        }

        return byScore;
    }

    public static double getMatchScore(ASTClassOrInterfaceBodyDeclaration aDecl, ASTClassOrInterfaceBodyDeclaration bDecl, MethodUtil methodUtil) {
        SimpleNode a = getDeclaration(aDecl);
        SimpleNode b = getDeclaration(bDecl);

        double score = 0.0;
        if (a == null && b == null) {
            score = 1.0;
        }
        else if (a == null || b == null) {
            // not a match.
        }
        else if (a.getClass().equals(b.getClass())) {
            if (a instanceof ASTMethodDeclaration) {
                if (methodUtil == null) {
                    methodUtil = new MethodUtil();
                }
                score = methodUtil.getMatchScore((ASTMethodDeclaration)a, (ASTMethodDeclaration)b);
            }
            else if (a instanceof ASTFieldDeclaration) {
                ASTFieldDeclaration afd = (ASTFieldDeclaration)a;
                ASTFieldDeclaration bfd = (ASTFieldDeclaration)b;
                
                score = FieldUtil.getMatchScore(afd, bfd);
            }
            else if (a instanceof ASTConstructorDeclaration) {
                score = CtorUtil.getMatchScore((ASTConstructorDeclaration)a, (ASTConstructorDeclaration)b);
            }
            else if (a instanceof ASTClassOrInterfaceDeclaration) {
                ASTClassOrInterfaceDeclaration acoid = (ASTClassOrInterfaceDeclaration)a;
                ASTClassOrInterfaceDeclaration bcoid = (ASTClassOrInterfaceDeclaration)b;
                score = ClassUtil.getMatchScore(acoid, bcoid);
            }
            else {
                // WTF?
                tr.Ace.stack(tr.Ace.RED, "a", a);
            }
        }

        return score;
    }

}
