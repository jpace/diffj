package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diffj.code.stmt.StatementListDiffer;
import org.incava.diffj.code.stmt.StatementsDiff;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.Node;

public class Block {
    private final String name;
    private final List<Statement> statements;
    private final AbstractJavaNode blk;

    public Block(String name, AbstractJavaNode blk) {
        this(name, blk, Node.of(blk).findChildren(ASTBlockStatement.class));
    }

    public Block(String name, AbstractJavaNode blk, List<ASTBlockStatement> blkStatements) {
        this.name = name;        
        this.statements = new ArrayList<Statement>();
        this.blk = blk;
        for (ASTBlockStatement blkStmt : blkStatements) {
            Statement stmt = new Statement(blkStmt);
            statements.add(stmt);
        }
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public Token getLastToken() {
        return Node.of(blk).getLastToken();
    }    

    public TokenList getTokens() {
        List<Token> allTokens = new ArrayList<Token>();
        for (Statement stmt : statements) {
            allTokens.addAll(stmt.getTokens());
        }
        return new TokenList(allTokens);
    }

    public void compareCode(Block toBlock, Differences differences) {
        compareCodeNew(toBlock, differences);
    }

    public void compareCodeOld(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }
    
    public void compareCodeNew(Block toBlock, Differences differences) {
        StatementListDiffer diff = new StatementListDiffer(this, toBlock);
        List<StatementsDiff> diffs = diff.execute();
        for (StatementsDiff df : diffs) {
            df.execute(name, differences);
        }
    }

    public String toString() {
        return "" + name;
    }
}
