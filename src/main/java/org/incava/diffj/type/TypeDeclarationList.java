package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class TypeDeclarationList extends ArrayList<ASTClassOrInterfaceBodyDeclaration> {
    private static final long serialVersionUID = 1L;
    
    /**
     * Initializes a list of all methods, fields, constructors, and inner
     * classes and interfaces for the class/interface declaration.
     */
    public TypeDeclarationList(ASTClassOrInterfaceDeclaration coid) {
        ASTClassOrInterfaceBody body = SimpleNodeUtil.findChild(coid, ASTClassOrInterfaceBody.class);
        List<ASTClassOrInterfaceBodyDeclaration> decls = SimpleNodeUtil.findChildren(body, ASTClassOrInterfaceBodyDeclaration.class);
        addAll(decls);
    }

    public <ItemType extends SimpleNode> List<ItemType> getDeclarationsOfClass(Class<ItemType> cls) {
        List<ItemType> declList = new ArrayList<ItemType>();

        for (ASTClassOrInterfaceBodyDeclaration decl : this) {
            ItemType dec = SimpleNodeUtil.hasChildren(decl) ? SimpleNodeUtil.findChild(decl, cls) : null;

            if (dec != null) {
                declList.add(dec);
            }   
        }
        
        return declList;
    }
}
