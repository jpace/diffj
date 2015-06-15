package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.code.statements.StatementListDiffer;
import org.incava.diffj.code.statements.StatementListDifference;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final List<Statement> statements;
    private final SimpleNode blk;

    public Block(String name, SimpleNode blk) {
        this(name, blk, SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class));
    }

    public Block(String name, SimpleNode blk, List<ASTBlockStatement> blkStatements) {
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
        return blk.getLastToken();
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
        List<StatementListDifference> diffs = diff.execute();
        for (StatementListDifference df : diffs) {
            df.execute(name, differences);
        }
    }
}
