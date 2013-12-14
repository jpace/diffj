package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import org.incava.diffj.type.Items;

public class Initializers extends Items<Initializer, ASTInitializer> {
    public Initializers(ASTClassOrInterfaceDeclaration typeDecl) {
        super(typeDecl, ASTInitializer.class);
    }

    public Initializer getAstType(ASTInitializer initDecl) {
        return new Initializer(initDecl);
    }
}
