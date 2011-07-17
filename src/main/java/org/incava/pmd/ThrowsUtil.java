package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for throws lists.
 */
public class ThrowsUtil extends SimpleNodeUtil {
    public static ASTName[] getNames(ASTNameList names) {
        return (ASTName[])findChildren(names, ASTName.class);
    }

    public static String getName(ASTNameList names, int index) {
        ASTName name = (ASTName)findChild(names, ASTName.class, index);
        return name == null ? null : toString(name);
    }

    public static ASTName getNameNode(ASTNameList names, int index) {
        return (ASTName)findChild(names, ASTName.class, index);
    }
}
