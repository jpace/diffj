package org.incava.diffj.element;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

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

    public static Object[] toParameters(SimpleNode a, SimpleNode b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(SimpleNodeUtil.toString(a));
        }
        if (b != null) {
            params.add(SimpleNodeUtil.toString(b));
        }
        return params.toArray(new Object[params.size()]);
    }
}
