package org.incava.diffj.field;

import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Modifiers;

public class FieldModifiers extends Modifiers {
    public static final int[] MODIFIERS = new int[] {
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC,
    };

    public FieldModifiers(SimpleNode node) {
        super(node);
    }

    public int[] getModifierTypes() {
        return MODIFIERS;
    }
}
