package org.incava.diffj.field;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserConstants;
import org.incava.diffj.element.Modifiers;
import org.incava.pmdx.Node;

public class FieldModifiers extends Modifiers {
    public static final int[] MODIFIERS = new int[] {
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC,
    };

    public FieldModifiers(AbstractJavaNode node) {
        super(node);
    }

    public int[] getModifierTypes() {
        return MODIFIERS;
    }
}
