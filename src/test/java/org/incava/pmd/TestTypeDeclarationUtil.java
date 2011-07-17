package org.incava.pmd;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.ijdk.lang.*;


public class TestTypeDeclarationUtil extends TestUtil {
    
    public TestTypeDeclarationUtil(String name) {
        super(name);
    }

    protected ASTTypeDeclaration[] parse(String contents, int numDecls) {
        JavaCharStream jcs = new JavaCharStream(new StringReader(contents));
        JavaParser p = new JavaParser(jcs);
        p.setJDK15();

        ASTCompilationUnit cu = p.CompilationUnit();
        ASTTypeDeclaration[] tdecls = CompilationUnitUtil.getTypeDeclarations(cu);

        assertEquals("#tdecls", numDecls, tdecls.length);
        
        return tdecls;
    }

    public void testClass() {
        String contents = (
            "public class Haughty {\n" +
            "    public void empty() {\n" +
            "    }\n" +
            "    \n" + 
            "    class Peas {\n" +
            "        boolean whirled = true;\n" +
            "    }\n" +
            "    \n" +
            "    String hi = \"lo\";\n" +
            "    \n" +
            "    interface Meeting {\n" +
            "        int timesBefore() { return 5; }\n" +
            "    }\n" +
            "    \n" +
            "}\n"
            );

        ASTTypeDeclaration[] types = parse(contents, 1);
        
        for (int ti = 0; ti < types.length; ++ti) {
            ASTTypeDeclaration type = types[ti];

            assertNotNull("type", type);
            
            Token nmtk = TypeDeclarationUtil.getName(type);
            assertNotNull("nmtk", nmtk);
            assertEquals("nmtk.image", "Haughty", nmtk.image);
            
            ASTClassOrInterfaceBodyDeclaration[] decls = TypeDeclarationUtil.getDeclarations(type);
            assertNotNull("decls", decls);
            assertEquals("#decls", 4, decls.length);

            for (int di = 0; di < decls.length; ++di) {
                ASTClassOrInterfaceBodyDeclaration decl = decls[di];
                assertNotNull("decl[" + di + "]", decl);
            }
        }
    }

    public void assertMatch(ASTClassOrInterfaceBodyDeclaration d0, 
                            ASTClassOrInterfaceBodyDeclaration d1, 
                            double expected) {
        double score = TypeDeclarationUtil.getMatchScore(d0, d1, null);
        assertEquals("score", expected, score, 0.0001);
    }

    public void testScores() {
        String contents0 = (
            "public class Haughty {\n" +
            "    void foo() {}\n" + // method
            "    Haughty() {}\n" + // ctor
            "    Haughty(Integer x, Object[] objs) {}\n" +
            "    void baz() {}\n" + // method
            "    int x;\n" +    // field
            "    Double x;\n" + // field
            "    class HIC1 {}\n" + // inner class
            "    class HIC2 {}\n" + // inner class
            "    interface II_1 {}\n" + // inner interface
            "    interface II_2 {}\n" + // inner interface
            "}\n"
            );
        ASTTypeDeclaration[] types0 = parse(contents0, 1);
        
        String contents1 = (
            "public class Haughty {\n" +
            "    Haughty() {}\n" +
            "    void foo() {}\n" +
            "    void bar() {}\n" +
            "    class Hick1 {}\n" + // inner class
            "    class HIC2 {}\n" + // inner class
            "    interface II_one {}\n" + // inner interface
            "    interface II_2 {}\n" + // inner interface
            "    double x;\n" +
            "    int y;\n" +
            "    Haughty(int x) {}\n" +
            "}\n"
            );
        ASTTypeDeclaration[] types1 = parse(contents1, 1);

        assertEquals("#types", types0.length, types1.length);

        final double[][] matches = new double[][] {
            { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5 },
            { 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 },
        };

        for (int ti = 0; ti < types0.length; ++ti) {
            ASTTypeDeclaration type0 = types0[ti];
            ASTTypeDeclaration type1 = types1[ti];

            ASTClassOrInterfaceBodyDeclaration[] decls0 = TypeDeclarationUtil.getDeclarations(type0);
            ASTClassOrInterfaceBodyDeclaration[] decls1 = TypeDeclarationUtil.getDeclarations(type1);

            for (int mi = 0; mi < matches.length; ++mi) {
                double[] expected = matches[mi];
                for (int ei = 0; ei < expected.length; ++ei) {
                    assertMatch(decls0[mi], decls1[ei], expected[ei]);
                }
            }            
        }
    }

    public void testMatch() {
        String contents0 = (
            "public class Haughty {\n" +
            "    void foo() {}\n" + // method
            "    Haughty() {}\n" + // ctor
            "    Haughty(Integer x, Object[] objs) {}\n" +
            "    void baz() {}\n" + // method
            "    int x;\n" +    // field
            "    Double x;\n" + // field
            "    class HIC1 {}\n" + // inner class
            "    class HIC2 {}\n" + // inner class
            "    interface II_1 {}\n" + // inner interface
            "    interface II_2 {}\n" + // inner interface
            "}\n"
            );
        ASTTypeDeclaration[] types0 = parse(contents0, 1);
        
        String contents1 = (
            "public class Haughty {\n" +
            "    Haughty() {}\n" +
            "    void foo() {}\n" +
            "    void bar() {}\n" +
            "    class Hick1 {}\n" + // inner class
            "    class HIC2 {}\n" + // inner class
            "    interface II_one {}\n" + // inner interface
            "    interface II_2 {}\n" + // inner interface
            "    double x;\n" +
            "    int y;\n" +
            "    Haughty(int x) {}\n" +
            "}\n"
            );
        ASTTypeDeclaration[] types1 = parse(contents1, 1);

        assertEquals("#types", types0.length, types1.length);

        final double[][] matches = new double[][] {
            { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5 },
            { 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 },
        };

        MethodUtil methodUtil = new MethodUtil();
        
        for (int ti = 0; ti < types0.length; ++ti) {
            ASTTypeDeclaration type0 = types0[ti];
            ASTTypeDeclaration type1 = types1[ti];

            ASTClassOrInterfaceBodyDeclaration[] decls0 = TypeDeclarationUtil.getDeclarations(type0);
            ASTClassOrInterfaceBodyDeclaration[] decls1 = TypeDeclarationUtil.getDeclarations(type1);

            Map<Double, List<Pair<SimpleNode, SimpleNode>>> declMap = TypeDeclarationUtil.matchDeclarations(decls0, decls1, methodUtil);

            for (Map.Entry<Double, List<Pair<SimpleNode, SimpleNode>>> entry : declMap.entrySet()) {
                Double score = entry.getKey();
                List<Pair<SimpleNode, SimpleNode>> decls = entry.getValue();
                
                for (Pair<SimpleNode, SimpleNode> values : decls) {
                    ASTClassOrInterfaceBodyDeclaration aDecl = (ASTClassOrInterfaceBodyDeclaration)values.getFirst();
                    ASTClassOrInterfaceBodyDeclaration bDecl = (ASTClassOrInterfaceBodyDeclaration)values.getSecond();
                    
                    SimpleNode a = TypeDeclarationUtil.getDeclaration(aDecl);
                    SimpleNode b = TypeDeclarationUtil.getDeclaration(bDecl);

                    assertEquals("classes", a.getClass(), b.getClass());
                }
            }
        }
    }

    public void testGetDeclarationsWithAnnotations() {
        String contents = ("public interface AS\n" +
                           "{\n" +
                           "  @Foo(name = \"Fred\")\n" +
                           "  Integer getFred();\n" +
                           "\n" +
                           "  void setFred(Integer fred);\n" +
                           "\n" +
                           "  @Foo(name = \"Ginger\", targetType = Integer.class)\n" +
                           "  Collection<Integer> getGinger();\n" +
                           "\n" +
                           "  void setGinger(Collection<Integer> ginger);\n" +
                           "}");

        tr.Ace.setVerbose(true);

        ASTTypeDeclaration[] types = parse(contents, 1);
        tr.Ace.yellow("types", types);
        
        for (ASTTypeDeclaration td : types) {
            tr.Ace.yellow("td", td);

            
        }
    }
}
