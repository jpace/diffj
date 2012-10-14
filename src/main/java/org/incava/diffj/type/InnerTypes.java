package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import org.incava.diffj.Items;

public class InnerTypes extends Items<Type, ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
    }

    public Type getAstType(ASTClassOrInterfaceDeclaration decl) {
        return new Type(decl);
    }
}
