package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.pmdx.TypeDeclarationUtil;
import org.incava.pmdx.Node;

public class TypeDeclarationList extends ArrayList<ASTClassOrInterfaceBodyDeclaration> {
    private static final long serialVersionUID = 1L;
    
    /**
     * Initializes a list of all methods, fields, constructors, and inner
     * classes and interfaces for the class/interface declaration.
     */
    public TypeDeclarationList(ASTClassOrInterfaceDeclaration coid) {
        ASTClassOrInterfaceBody body = Node.of(coid).findChild(ASTClassOrInterfaceBody.class);
        List<ASTClassOrInterfaceBodyDeclaration> decls = Node.of(body).findChildren(ASTClassOrInterfaceBodyDeclaration.class);
        addAll(decls);
    }

    public <ItemType extends AbstractJavaNode> List<ItemType> getDeclarationsOfClass(Class<ItemType> cls) {
        List<ItemType> declList = new ArrayList<ItemType>();

        for (ASTClassOrInterfaceBodyDeclaration decl : this) {
            ItemType dec = Node.of(decl).hasChildren() ? Node.of(decl).findChild(cls) : null;
            if (dec != null) {
                declList.add(dec);
            }   
        }
        
        return declList;
    }
}
