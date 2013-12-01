package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final ASTBlock blk;
    private final List<Statement> statements;
    private final TokenList tokens;

    public Block(String name, ASTBlock blk) {
        this.name = name;
        this.blk = blk;
        List<ASTBlockStatement> stmts = SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class);
        
        tr.Ace.bold("stmts", stmts);
        // this.tokens = new TokenList(blk);
        List<Token> allTokens = new java.util.ArrayList<Token>();
        this.statements = new ArrayList<Statement>();
        for (ASTBlockStatement stmt : stmts) {
            allTokens.addAll(SimpleNodeUtil.getChildTokens(stmt));
            statements.add(new Statement(stmt));
        }
        this.tokens = new TokenList(allTokens);
        tr.Ace.bold("tokens", tokens);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void compareCode(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, tokens);
        Code toCode = new Code(name, toBlock.tokens);
        fromCode.diff(toCode, differences);
    }
}
