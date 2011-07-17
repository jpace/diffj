package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;


public class TestJavaParser extends TestCase {

    public TestJavaParser(String name) {
        super(name);
    }

//     protected void checkToken(Token tk, int index, int expectedType, int[] expectedPositions)
//     {
//         assertEquals("token[" + index + "].type (" + Token.names[expectedType] + ")", expectedType, tk.getType());

//         assertEquals("token " + index + " begin line number",   expectedPositions[0], tk.beginLine);
//         assertEquals("token " + index + " begin column number", expectedPositions[1], tk.beginColumn);
//         assertEquals("token " + index + " end line number",     expectedPositions[2], tk.endLine);
//         assertEquals("token " + index + " end column number",   expectedPositions[3], tk.endColumn);
//     }

//     public List toTokens(String str, boolean isJava14)
//     {
//         tr.Ace.log("str: " + str);
//         StringReader rdr = new StringReader(str);
//         Scanner scanner = new Scanner(rdr);

//         List tokens = new ArrayList();
//         Token tk = scanner.tokenize();
//         tr.Ace.log("token", tk);

//         while (tk != null) {
//             tokens.add(tk);
//             tk = tk.nextToken;
//         }
        
//         return tokens;
//     }

//     public void runTest(String contents, int[] expectedTokens, int[][] expectedPositions, boolean isJava14)
//     {
//         List tokens = toTokens(contents, isJava14);
//         assertEquals("number of tokens", expectedTokens.length, tokens.size());
//         for (int ti = 0; ti < expectedTokens.length; ++ti) {
//             Token tk = (Token)tokens.get(ti);
//             checkToken(tk, ti, expectedTokens[ti], expectedPositions[ti]);
//         }
//     }

//     public void runTest(String contents, int[] expectedTokens, int[][] positions)
//     {
//         runTest(contents, expectedTokens, positions, true);
//     }

//     public void testPackage()
//     {
//         String contents = ("package org.incava.pmd2;");
//         Scanner s = new Scanner(new StringReader(contents), true);
//         Token tk = s.tokenize();
//         JavaParser p = new JavaParser(tk);
//         NCompilationUnit cu = p.parse();
//         assertNotNull("compilation unit", cu);
//         assertNotNull("cu.package", cu.nPackage);
//         assertNotNull("cu.package.package", cu.nPackage.tPackage);
//         assertNotNull("cu.package.name", cu.nPackage.nName);
//         assertNotNull("cu.package.sc", cu.nPackage.tSemicolon);
//     }

//     public void testImportSingle()
//     {
//         String contents = ("import org.incava.Foo;");
//         Scanner s = new Scanner(new StringReader(contents), true);
//         Token tk = s.tokenize();
//         JavaParser p = new JavaParser(tk);
//         NCompilationUnit cu = p.parse();
//         assertNotNull("compilation unit", cu);
//         assertNotNull("cu.imports", cu.nImports);
//         assertEquals("cu.imports", 1, cu.nImports.length);
//         assertNotNull("cu.imports.0.package", cu.nImports[0].tImport);
//         assertNotNull("cu.imports.0.name", cu.nImports[0].nName);
//         assertNotNull("cu.imports.0.sc", cu.nImports[0].tSemicolon);
//     }

    public void testTypeClass() {
        String contents = ("package org.incava.testing;\n" + 
                           "import org.incava.imptd.*;\n" + 
                           "public abstract strictfp final class Foo {}");

        JavaCharStream jcs = new JavaCharStream(new StringReader(contents));
        JavaParser p = new JavaParser(jcs);

        ASTCompilationUnit cu = p.CompilationUnit();

        tr.Ace.log("cu", cu);

        assertNotNull("compilation unit", cu);
        ASTPackageDeclaration pkg = CompilationUnitUtil.getPackage(cu);
        tr.Ace.log("pkg", pkg);
        assertNotNull("pkg", pkg);

        ASTImportDeclaration[] imports = CompilationUnitUtil.getImports(cu);
        tr.Ace.log("imports", imports);
        assertNotNull("imports", imports);
    }

}
