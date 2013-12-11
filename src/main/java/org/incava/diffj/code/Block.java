package org.incava.diffj.code;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.*;
import org.incava.pmdx.SimpleNodeUtil;

public class Block {
    private final String name;
    private final List<Statement> statements;

    public Block(String name, SimpleNode blk) {
        this(name, SimpleNodeUtil.findChildren(blk, ASTBlockStatement.class));
    }

    public Block(String name, List<ASTBlockStatement> blkStatements) {
        this.name = name;        
        this.statements = new ArrayList<Statement>();
        for (ASTBlockStatement blkStmt : blkStatements) {
            Statement stmt = new Statement(blkStmt);
            statements.add(stmt);
        }
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public TokenList getTokens() {
        List<Token> allTokens = new ArrayList<Token>();
        for (Statement stmt : statements) {
            allTokens.addAll(stmt.getTokens());
        }
        return new TokenList(allTokens);
    }

    public List<TokenList> getTokenLists() {
        List<TokenList> tokenLists = new ArrayList<TokenList>();
        for (Statement stmt : statements) {
            tokenLists.add(new TokenList(stmt.getTokens()));
        }
        return tokenLists;
    }

    public void compareCode(Block toBlock, Differences differences) {
        compareCodeNew(toBlock, differences);
    }

    public void compareCodeOld(Block toBlock, Differences differences) {
        Code fromCode = new Code(name, getTokens());
        Code toCode = new Code(name, toBlock.getTokens());
        fromCode.diff(toCode, differences);
    }

    public TokenList getAsTokenList(List<TokenList> tokenLists, int from, int to) {
        int idx = from;
        TokenList list = tokenLists.get(idx++);
        while (idx <= to) {
            list.add(tokenLists.get(idx++));
        }

        return list;
    }

    public void processAddedStatements(List<TokenList> fromTokenLists, 
                                       List<TokenList> toTokenLists, 
                                       Difference df, Differences differences) {
        tr.Ace.onRed("df", df);
        TokenList alist = getAsTokenList(fromTokenLists, df.getDeletedStart(), df.getDeletedEnd());
        tr.Ace.log("alist", alist);

        LocationRange flr = alist.getLocationRange(0, Difference.NONE);
        tr.Ace.cyan("flr", flr);
        
        TokenList blist = getAsTokenList(toTokenLists, df.getAddedStart(), df.getAddedEnd());
        LocationRange tlr = blist.getAsLocationRange();
        tr.Ace.cyan("tlr", tlr);

        tr.Ace.log("alist", alist);
        tr.Ace.log("blist", blist);

        String str = Code.CODE_ADDED.format(name);        
        FileDiff fileDiff = new FileDiffCodeAdded(str, flr, tlr);
        differences.add(fileDiff);
    }

    public void processChangedStatements(List<TokenList> fromTokenLists,
                                         List<TokenList> toTokenLists, 
                                         Difference df, Differences differences) {
        TokenList alist = getAsTokenList(fromTokenLists, df.getDeletedStart(), df.getDeletedEnd());        
        TokenList blist = getAsTokenList(toTokenLists, df.getAddedStart(), df.getAddedEnd());

        tr.Ace.log("alist", alist);
        tr.Ace.log("blist", blist);

        Code fc = new Code(name, alist);
        tr.Ace.log("fc", fc);
        Code tc = new Code(name, blist);
        tr.Ace.log("tc", tc);

        fc.diff(tc, differences);
    }

    public void processDeletedStatements(List<TokenList> fromTokenLists,
                                         List<TokenList> toTokenLists, 
                                         Difference df, Differences differences) {
        tr.Ace.onRed("df", df);
        TokenList alist = getAsTokenList(fromTokenLists, df.getDeletedStart(), df.getDeletedEnd());        
        LocationRange flr = alist.getAsLocationRange();
        tr.Ace.cyan("flr", flr);

        TokenList blist = getAsTokenList(toTokenLists, df.getAddedStart(), df.getAddedEnd());
        LocationRange tlr = blist.getLocationRange(0, Difference.NONE);
        
        tr.Ace.cyan("tlr", tlr);

        tr.Ace.log("alist", alist);
        tr.Ace.log("blist", blist);

        String str = Code.CODE_REMOVED.format(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(str, flr, tlr);
        differences.add(fileDiff);
    }
    
    public void compareCodeNew(Block toBlock, Differences differences) {
        List<TokenList> fromTokenLists = getTokenLists();
        List<TokenList> toTokenLists = toBlock.getTokenLists();
        tr.Ace.cyan("fromTokenLists", fromTokenLists);
        tr.Ace.yellow("toTokenLists", toTokenLists);

        Diff<TokenList> diff = new Diff<TokenList>(fromTokenLists, toTokenLists);
        List<Difference> diffs = diff.execute();
        tr.Ace.log("diffs", diffs);
        for (Difference df : diffs) {
            tr.Ace.yellow("df", df);
            tr.Ace.yellow("df.add?", df.isAdd());
            tr.Ace.yellow("df.change?", df.isChange());
            tr.Ace.yellow("df.delete?", df.isDelete());
            if (df.isAdd()) {
                processAddedStatements(fromTokenLists, toTokenLists, df, differences);
            }
            else if (df.isChange()) {
                processChangedStatements(fromTokenLists, toTokenLists, df, differences);
            }
            else if (df.isDelete()) {
                processDeletedStatements(fromTokenLists, toTokenLists, df, differences);
            }
        }
    }
}
