package org.incava.diffj.type;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Types {
    public static final Message TYPE_DECLARATION_ADDED = new Message("type declaration added: {0}");
    public static final Message TYPE_DECLARATION_REMOVED = new Message("type declaration removed: {0}");

    private final ASTCompilationUnit compUnit;
    private final List<ASTTypeDeclaration> types;

    public Types(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.types = CompilationUnitUtil.getTypeDeclarations(compUnit);
    }

    public void diff(Types toTypes, Differences differences) {
        Collection<String> names = getNames();
        names.addAll(toTypes.getNames());

        for (String name : names) {
            ASTTypeDeclaration fromTypeDecl = getDeclaration(name);
            ASTTypeDeclaration toTypeDecl = toTypes.getDeclaration(name);
            compareTypes(fromTypeDecl, toTypeDecl, toTypes, differences);
        }
    }

    protected void compareTypes(ASTTypeDeclaration fromTypeDecl, ASTTypeDeclaration toTypeDecl, Types toTypes, Differences differences) {
        if (fromTypeDecl == null) {
            Token toName = TypeDeclarationUtil.getName(toTypeDecl);
            differences.added(compUnit, toTypeDecl, TYPE_DECLARATION_ADDED, toName.image);
        }
        else if (toTypeDecl == null) {
            Token toName = TypeDeclarationUtil.getName(fromTypeDecl);
            differences.deleted(fromTypeDecl, toTypes.compUnit, TYPE_DECLARATION_REMOVED, toName.image);
        }
        else {
            ASTClassOrInterfaceDeclaration fromDecl = TypeDeclarationUtil.getType(fromTypeDecl);
            ASTClassOrInterfaceDeclaration toDecl = TypeDeclarationUtil.getType(toTypeDecl);

            if (fromDecl != null && toDecl != null) {
                Type fromType = new Type(fromDecl);
                Type toType = new Type(toDecl);
                fromType.diff(toType, differences);
            }
        }
    }

    protected Collection<String> getNames() {
        Collection<String> names = new TreeSet<String>();
        for (ASTTypeDeclaration type : types) {
            Token tk = TypeDeclarationUtil.getName(type);
            if (tk != null) {
                names.add(tk.image);
            }
        }
        return names;
    }

    protected ASTTypeDeclaration getDeclaration(String name) {
        for (ASTTypeDeclaration type : types) {
            Token tk = TypeDeclarationUtil.getName(type);
            if (tk != null && name.equals(tk.image)) {
                return type;
            }
        }
        return null;
    }
}
