package org.incava.diffj.code;

import java.util.*;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final ASTBlock blk;
    private final List<SimpleNode> statements;
    private final TokenList tokens;

    public Block(String name, ASTBlock blk) {
        this.name = name;
        this.blk = blk;
        this.statements = SimpleNodeUtil.findChildren(blk);
        this.tokens = new TokenList(blk);
    }

    public Block(String name, TokenList tokens) {
        this.name = name;
        this.blk = null;
        this.statements = null;
        this.tokens = tokens;
    }

    public TokenList getCodeTokens() {
        return tokens;
    }

    public List<SimpleNode> getStatements() {
        return statements;
    }

    public void compareCode(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getCodeTokens());
        Code toCode = new Code(name, toBlock.getCodeTokens());
        fromCode.diff(toCode, differences);
    }
}
