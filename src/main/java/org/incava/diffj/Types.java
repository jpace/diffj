package org.incava.diffj;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Types {
    private final ASTCompilationUnit compUnit;

    public Types(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void diff(ASTCompilationUnit toCompUnit, Differences differences) {
        List<ASTTypeDeclaration> fromTypes = CompilationUnitUtil.getTypeDeclarations(compUnit);
        List<ASTTypeDeclaration> toTypes = CompilationUnitUtil.getTypeDeclarations(toCompUnit);

        Map<String, ASTTypeDeclaration> fromNamesToTD = makeTDMap(fromTypes);
        Map<String, ASTTypeDeclaration> toNamesToTD = makeTDMap(toTypes);

        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToTD.keySet());
        names.addAll(toNamesToTD.keySet());

        for (String name : names) {
            ASTTypeDeclaration fromTypeDecl  = fromNamesToTD.get(name);
            ASTTypeDeclaration toTypeDecl  = toNamesToTD.get(name);

            if (fromTypeDecl == null) {
                Token toName = TypeDeclarationUtil.getName(toTypeDecl);
                differences.added(compUnit, toTypeDecl, Messages.TYPE_DECLARATION_ADDED, toName.image);
            }
            else if (toTypeDecl == null) {
                Token toName = TypeDeclarationUtil.getName(fromTypeDecl);
                differences.deleted(fromTypeDecl, toCompUnit, Messages.TYPE_DECLARATION_REMOVED, toName.image);
            }
            else {
                Type type = new Type(differences.getFileDiffs());
                type.compare(fromTypeDecl, toTypeDecl);
            }
        }
    }

    protected Map<String, ASTTypeDeclaration> makeTDMap(List<ASTTypeDeclaration> types) {
        Map<String, ASTTypeDeclaration> namesToTD = new HashMap<String, ASTTypeDeclaration>();
        for (ASTTypeDeclaration type : types) {
            Token tk = TypeDeclarationUtil.getName(type);
            if (tk != null) {
                namesToTD.put(tk.image, type);
            }
        }
        return namesToTD;
    }
}
