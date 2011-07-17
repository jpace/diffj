package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;


/**
 * Miscellaneous routines for fields.
 */
public class FieldUtil extends SimpleNodeUtil {
    public static Token getName(ASTVariableDeclarator vd) {
        ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)findChild(vd, ASTVariableDeclaratorId.class);
        Token nameTk = vid.getFirstToken();
        return nameTk;
    }

    public static ASTVariableDeclarator[] getVariableDeclarators(ASTFieldDeclaration fld) {
        return (ASTVariableDeclarator[])findChildren(fld, ASTVariableDeclarator.class);
    }

    /**
     * Returns a string in the form "a, b, c", for the variables declared in
     * this field.
     */
    public static String getNames(ASTFieldDeclaration fld) {
        return StringExt.join(getNameList(fld), ", ");
    }

    /**
     * Returns a list of strings of the names of the variables declared in this
     * field.
     */
    public static List<String> getNameList(ASTFieldDeclaration fld) {
        List<String> names = new ArrayList<String>();
        for (ASTVariableDeclarator avd : getVariableDeclarators(fld)) {
            names.add(VariableUtil.getName(avd).image);
        }
        return names;
    }

    public static double getMatchScore(ASTFieldDeclaration afd, ASTFieldDeclaration bfd) {
        // a field can have more than one name.

        List<String> aNames = FieldUtil.getNameList(afd);
        List<String> bNames = FieldUtil.getNameList(bfd);

        Set<String> inBoth = CollectionExt.intersection(FieldUtil.getNameList(afd), FieldUtil.getNameList(bfd));

        int     matched = inBoth.size();
        int     count   = Math.max(aNames.size(), bNames.size());
        double  score   = 0.5 * matched / count;

        ASTType aType   = (ASTType)findChild(afd, ASTType.class);
        ASTType bType   = (ASTType)findChild(bfd, ASTType.class);

        if (toString(aType).equals(toString(bType))) {
            score += 0.5;
        }
        
        return score;
    }
}
