package org.incava.diffj.function;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.code.Block;
import org.incava.diffj.code.Statement;
import org.incava.diffj.code.TokenList;
import org.incava.diffj.compunit.CompilationUnit;
import org.incava.diffj.function.Method;
import org.incava.diffj.io.JavaFile;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.text.Location;
import org.incava.pmdx.Node;
import org.incava.pmdx.SimpleNodeUtil;

import static org.incava.diffj.code.Code.*;

public class MethodCodeByStatementTest extends ItemsTest {
    public MethodCodeByStatementTest(String name) {
        super(name);
    }

    public URL seek(String name) {
        return ClassLoader.getSystemResource(name);
    }

    public TokenList dumpTokens(AbstractJavaNode node) {
        TokenList tokens = new TokenList(node);
        tr.Ace.log("tokens", tokens);
        String str = tokens.toString();
        return tokens;
    }

    public AbstractJavaNode getChildNode(AbstractJavaNode parent, int idx) {
        return Node.of(parent).findChild(null, idx);
    }

    public AbstractJavaNode getDescendantNode(AbstractJavaNode parent, int depth) {
        if (depth <= 0) {
            return null;
        }
        else {
            AbstractJavaNode child = Node.of(parent).findChild(null, 0);
            return depth == 1 ? child : getDescendantNode(child, depth - 1);
        }
    }

    public CompilationUnit getCompilationUnit(String fileName) throws Exception {
        URL url = seek(fileName);
        File file = new File(url.toURI());
        JavaFile javaFile = new JavaFile(file, fileName, "1.6");
        return javaFile.compile();
    }

    /**
     * Returns the first method. Assumes type->class->body->method.
     */
    public ASTMethodDeclaration getFirstMethod(ASTCompilationUnit ast) {
        AbstractJavaNode typeDecl = getChildNode(ast, 0);
        AbstractJavaNode clsDecl = getChildNode(typeDecl, 0);
        AbstractJavaNode body = getChildNode(clsDecl, 0);
        AbstractJavaNode bodyDecl = getChildNode(body, 0);
        return (ASTMethodDeclaration)getChildNode(bodyDecl, 0);
    }

    public List<ASTBlockStatement> getStatements(AbstractJavaNode node) {
        return Node.of(node).findChildren(ASTBlockStatement.class);
    }

    public List<TokenList> showMethod(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        ASTMethodDeclaration methNode = getFirstMethod(ast);
        Method meth = new Method(methNode);
        Block methBlk = meth.getBlock();
        tr.Ace.onRed("methBlk", methBlk);
        meth.dump();

        ASTBlock astBlk = Node.of(methNode).findChild(ASTBlock.class);
        List<Token> tokens = Node.of(astBlk).getChildTokens();
        tr.Ace.log("tokens", tokens);

        List<TokenList> tokenLists = new ArrayList<TokenList>();

        List<Statement> statements = methBlk.getStatements();
        for (Statement stmt : statements) {
            tr.Ace.yellow("stmt", stmt);
            ASTBlockStatement blkStmt = stmt.getBlockStatement();
            dumpTokens(blkStmt);
            TokenList stmtTokenList = stmt.getTokenList();
            tr.Ace.log("stmtTokenList", stmtTokenList);
            
            tokenLists.add(stmtTokenList);
        }
        return tokenLists;
    }

    public void testMethod() throws Exception {
        tr.Ace.onBlue("******************************************");
        List<TokenList> a = showMethod("diffj/codecomp/d0/Changed.java");
        List<TokenList> b = showMethod("diffj/codecomp/d1/Changed.java");
        tr.Ace.onBlue("******************************************");
    }

    public List<TokenList> showCtor(String fileName) throws Exception {
        ASTCompilationUnit ast = getCompilationUnit(fileName).getAstCompUnit();
        AbstractJavaNode typeDecl = getChildNode(ast, 0);
        AbstractJavaNode clsDecl = getChildNode(typeDecl, 0);
        AbstractJavaNode body = getChildNode(clsDecl, 0);
        AbstractJavaNode bodyDecl = getChildNode(body, 0);
        AbstractJavaNode ctorDecl = getChildNode(bodyDecl, 0);
        
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
        tr.Ace.onGreen("******************************************");
    }
}
