package org.incava.diffj.code;

import java.util.*;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final ASTBlock blk;
    private final List<SimpleNode> statements;
    private final TokenList tokens;

    public Block(ASTBlock blk) {
        this.blk = blk;
        statements = SimpleNodeUtil.findChildren(blk);
        tokens = new TokenList(blk);
    }

    public Block(TokenList tokens) {
        blk = null;
        statements = null;
        this.tokens = tokens;
    }

    public TokenList getCodeTokens() {
        return tokens;
    }

    public List<SimpleNode> getStatements() {
        return statements;
    }
}
