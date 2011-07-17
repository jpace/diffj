package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.pmd.*;


public class TypesDiff extends ItemDiff {
    public static final String TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface";

    public static final String TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class";

    public static final String TYPE_DECLARATION_ADDED = "type declaration added: {0}";

    public static final String TYPE_DECLARATION_REMOVED = "type declaration removed: {0}";

    public TypesDiff(Collection<FileDiff> differences) {
        super(differences);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        ASTTypeDeclaration[] aTypes = CompilationUnitUtil.getTypeDeclarations(a);
        ASTTypeDeclaration[] bTypes = CompilationUnitUtil.getTypeDeclarations(b);
        
        // tr.Ace.log("aTypes", aTypes);
        // tr.Ace.log("bTypes", bTypes);

        Map<String, ASTTypeDeclaration> aNamesToTD = makeTDMap(aTypes);
        Map<String, ASTTypeDeclaration> bNamesToTD = makeTDMap(bTypes);

        // tr.Ace.log("aNamesToTD", aNamesToTD);
        // tr.Ace.log("bNamesToTD", bNamesToTD);

        Collection<String> names = new TreeSet<String>();
        names.addAll(aNamesToTD.keySet());
        names.addAll(bNamesToTD.keySet());

        for (String name : names) {
            ASTTypeDeclaration atd  = aNamesToTD.get(name);
            ASTTypeDeclaration btd  = bNamesToTD.get(name);

            // tr.Ace.log("name", name);
            // tr.Ace.log("atd", atd);
            // tr.Ace.log("btd", btd);

            if (atd == null) {
                Token bName = TypeDeclarationUtil.getName(btd);
                added(a, btd, TYPE_DECLARATION_ADDED, bName.image);
            }
            else if (btd == null) {
                Token aName = TypeDeclarationUtil.getName(atd);
                deleted(atd, b, TYPE_DECLARATION_REMOVED, aName.image);
            }
            else {
                TypeDiff differ = new TypeDiff(getFileDiffs());
                differ.compare(atd, btd);
            }
        }
    }

    protected Map<String, ASTTypeDeclaration> makeTDMap(ASTTypeDeclaration[] types) {
        Map<String, ASTTypeDeclaration> namesToTD = new HashMap<String, ASTTypeDeclaration>();
        for (int i = 0; i < types.length; ++i) {
            ASTTypeDeclaration type = types[i];
            Token              tk   = TypeDeclarationUtil.getName(type);
            if (tk != null) {
                namesToTD.put(tk.image, type);
            }
        }

        return namesToTD;
    }

}
