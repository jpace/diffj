package org.incava.diffj;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Types {
    private final ASTCompilationUnit compUnit;
    private final List<ASTTypeDeclaration> types;

    public Types(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.types = CompilationUnitUtil.getTypeDeclarations(compUnit);
    }

    public void diff(ASTCompilationUnit toCompUnit, Differences differences) {
        Types toTypes = new Types(toCompUnit);

        Map<String, ASTTypeDeclaration> fromNamesToTD = getNamesToDeclarations();
        Map<String, ASTTypeDeclaration> toNamesToTD = toTypes.getNamesToDeclarations();

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
                ASTClassOrInterfaceDeclaration fromDecl = TypeDeclarationUtil.getType(fromTypeDecl);
                ASTClassOrInterfaceDeclaration toDecl = TypeDeclarationUtil.getType(toTypeDecl);

                // either is null 
                if (fromDecl != null && toDecl != null) {
                    // Type type = new Type(fromDecl);
                    Type type = new Type(fromDecl);
                    type.diff(fromDecl, toDecl, differences);
                }
            }
        }
    }

    protected Map<String, ASTTypeDeclaration> getNamesToDeclarations() {
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
