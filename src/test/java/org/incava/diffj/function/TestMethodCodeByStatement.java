package org.incava.diffj.function;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.code.TokenDifference;
import org.incava.diffj.compunit.CompilationUnit;
import org.incava.diffj.function.Method;
import org.incava.diffj.io.JavaFile;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.util.diff.*;
import org.incava.java.Java;
import org.incava.pmdx.SimpleNodeUtil;
import static org.incava.diffj.code.Code.*;

public class TestMethodCodeByStatement extends ItemsTest {
    public TestMethodCodeByStatement(String name) {
        super(name);
        tr.Ace.setVerbose(true);
        tr.Ace.log("name", name);
    }

    public URL seek(String name) {
        return ClassLoader.getSystemResource(name);
    }

    public void dump(SimpleNode node) {
        SimpleNodeUtil.dump(node);
    }

    public TokenList dumpTokens(SimpleNode node) {
        TokenList tokens = new TokenList(node);
        tr.Ace.log("tokens", tokens);
        String str = tokens.toString();
        return tokens;
    }

    public SimpleNode getChildNode(SimpleNode parent, int idx) {
        return SimpleNodeUtil.findChild(parent, null, idx);
    }

    public CompilationUnit getCompilationUnit(String fileName) throws Exception {
        URL url = seek(fileName);
        File file = new File(url.toURI());
        JavaFile javaFile = new JavaFile(file, fileName, "1.5");
        return javaFile.compile();
    }

    /**
     * Returns the first method. Assumes type->class->body->method.
     */
    public SimpleNode getFirstMethod(ASTCompilationUnit ast) {
        SimpleNode typeDecl = getChildNode(ast, 0);
        dump(typeDecl);
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);
        SimpleNode bodyDecl = getChildNode(body, 0);
        return getChildNode(bodyDecl, 0);
    }

    public List<ASTBlockStatement> getStatements(SimpleNode node) {
        return SimpleNodeUtil.findChildren(node, ASTBlockStatement.class);
    }

    public List<TokenList> showMethod(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        SimpleNode methNode = getFirstMethod(ast);
        tr.Ace.onRed("methNode", methNode);
        Method meth = new Method((net.sourceforge.pmd.ast.ASTMethodDeclaration)methNode);
        // SimpleNode methBlk = getChildNode(methNode, 2);
        Block methBlk = meth.getBlock();
        tr.Ace.onRed("methBlk", methBlk);
        // dump(methBlk);

        ASTBlock astBlk = SimpleNodeUtil.findChild(methNode, ASTBlock.class);
        List<Token> tokens = SimpleNodeUtil.getChildTokens(astBlk);
        tr.Ace.log("tokens", tokens);

        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<Statement> statements = methBlk.getStatements();
        for (Statement stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            ASTBlockStatement blkStmt = stmt.getBlockStatement();
            dumpTokens(blkStmt);
            List<Token> stmtTokens = stmt.getTokens();
            tr.Ace.log("stmtTokens", stmtTokens);
            
            tokenLists.add(new TokenList(stmtTokens));
        }
        return tokenLists;
    }

    public void runDiff(List<TokenList> a, List<TokenList> b) {
        Diff<TokenList> diff = new Diff<TokenList>(a, b);
        List<Difference> diffs = diff.execute();
        tr.Ace.log("diffs", diffs);
        for (Difference df : diffs) {
            tr.Ace.yellow("df", df);
            tr.Ace.yellow("df.add?", df.isAdd());
            tr.Ace.yellow("df.change?", df.isChange());
            tr.Ace.yellow("df.delete?", df.isDelete());
            if (df.isChange()) {
                TokenList alist = a.get(df.getDeletedStart());
                tr.Ace.log("alist", alist);
                TokenList blist = b.get(df.getAddedStart());
                tr.Ace.log("blist", blist);

                Differ<Token, TokenDifference> differ = alist.diff(blist);
                tr.Ace.log("differ", differ);
                List<TokenDifference> tkDiffs = differ.execute();
                tr.Ace.log("tkDiffs", tkDiffs);
                
                // Differ<Token, TokenDifference> diff(TokenList toTokenList) 
            }
        }
    }

    public void testMethod() throws Exception {
        tr.Ace.onBlue("******************************************");
        List<TokenList> a = showMethod("diffj/codecomp/d0/Changed.java");
        List<TokenList> b = showMethod("diffj/codecomp/d1/Changed.java");
        runDiff(a, b);
        tr.Ace.onBlue("******************************************");
    }

    public List<TokenList> showCtor(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        SimpleNode typeDecl = getChildNode(ast, 0);
        dump(typeDecl);
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);
        SimpleNode bodyDecl = getChildNode(body, 0);
        SimpleNode ctorDecl = getChildNode(bodyDecl, 0);

        // dump(ctorDecl);
        
        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<ASTBlockStatement> statements = getStatements(ctorDecl);
        // the parameters list:
        statements.remove(0);
        for (ASTBlockStatement stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            dumpTokens(stmt);
            tokenLists.add(new TokenList(stmt));
        }
        return tokenLists;
    }

    public void testCtor() throws Exception {
        tr.Ace.onGreen("******************************************");
        List<TokenList> a = showCtor("diffj/codecomp/d0/ChangedCtor.java");
        List<TokenList> b = showCtor("diffj/codecomp/d1/ChangedCtor.java");
        runDiff(a, b);
        tr.Ace.onGreen("******************************************");
    }
}
