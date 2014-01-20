package org.incava.diffj.code;

import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.util.DefaultComparator;

public class TokenComparator extends DefaultComparator<Token> {
    public int doCompare(Token xt, Token yt) {
        Tkn x = new Tkn(xt);
        Tkn y = new Tkn(yt);
        return x.compareTo(y);
    }
}
