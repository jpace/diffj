package org.incava.diffj.element;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.pmdx.Node;

public class DiffParameters {
    public static Object[] toParameters(Token a, Token b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(a.image);
        }
        if (b != null) {
            params.add(b.image);
        }
        return params.toArray(new Object[params.size()]);
    }

    public static Object[] toParameters(AbstractJavaNode a, AbstractJavaNode b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(Node.of(a).toString());
        }
        if (b != null) {
            params.add(Node.of(b).toString());
        }
        return params.toArray(new Object[params.size()]);
    }
}
