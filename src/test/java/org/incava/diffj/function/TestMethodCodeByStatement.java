package org.incava.diffj.function;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.compunit.CompilationUnit;
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
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);
        SimpleNode bodyDecl = getChildNode(body, 0);
        return getChildNode(bodyDecl, 0);
    }

    public List<SimpleNode> getStatements(SimpleNode node) {
        return SimpleNodeUtil.findChildren(node);
    }

    public List<TokenList> show(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        SimpleNode meth = getFirstMethod(ast);
        SimpleNode methBlk = getChildNode(meth, 2);
        dump(methBlk);

        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<SimpleNode> statements = getStatements(methBlk);
        for (SimpleNode stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            dumpTokens(stmt);
            tokenLists.add(new TokenList(stmt));
        }
        return tokenLists;
    }

    public void testMethod() throws Exception {
        tr.Ace.onBlue("******************************************");
        List<TokenList> a = show("diffj/codecomp/d0/Changed.java");
        List<TokenList> b = show("diffj/codecomp/d1/Changed.java");
        Diff<TokenList> diff = new Diff<TokenList>(a, b, new TokenList.TokenListComparator());
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
                
                // Differ<Token, TokenDifference> diff(TokenList toTokenList) 
            }
        }
        tr.Ace.onBlue("******************************************");
    }

    public List<TokenList> showCtor(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        SimpleNode typeDecl = getChildNode(ast, 0);
        SimpleNode clsDecl = getChildNode(typeDecl, 0);
        SimpleNode body = getChildNode(clsDecl, 0);
        SimpleNode bodyDecl = getChildNode(body, 0);
        SimpleNode ctorDecl = getChildNode(bodyDecl, 0);

        dump(ctorDecl);
        
        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<SimpleNode> statements = getStatements(ctorDecl);
        // the parameters list:
        statements.remove(0);
        for (SimpleNode stmt : statements) {
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
        Diff<TokenList> diff = new Diff<TokenList>(a, b, new TokenList.TokenListComparator());
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
                
                // Differ<Token, TokenDifference> diff(TokenList toTokenList) 
            }
        }
        tr.Ace.onGreen("******************************************");
    }
}
