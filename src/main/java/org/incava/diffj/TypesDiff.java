package org.incava.diffj;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class TypesDiff extends ItemDiff {
    public TypesDiff(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        List<ASTTypeDeclaration> aTypes = CompilationUnitUtil.getTypeDeclarations(a);
        List<ASTTypeDeclaration> bTypes = CompilationUnitUtil.getTypeDeclarations(b);

        Map<String, ASTTypeDeclaration> aNamesToTD = makeTDMap(aTypes);
        Map<String, ASTTypeDeclaration> bNamesToTD = makeTDMap(bTypes);

        Collection<String> names = new TreeSet<String>();
        names.addAll(aNamesToTD.keySet());
        names.addAll(bNamesToTD.keySet());

        for (String name : names) {
            ASTTypeDeclaration atd  = aNamesToTD.get(name);
            ASTTypeDeclaration btd  = bNamesToTD.get(name);

            if (atd == null) {
                Token bName = TypeDeclarationUtil.getName(btd);
                added(a, btd, Messages.TYPE_DECLARATION_ADDED, bName.image);
            }
            else if (btd == null) {
                Token aName = TypeDeclarationUtil.getName(atd);
                deleted(atd, b, Messages.TYPE_DECLARATION_REMOVED, aName.image);
            }
            else {
                TypeDiff differ = new TypeDiff(getFileDiffs());
                differ.compare(atd, btd);
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
