package org.incava.diffj.compunit;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.java.Java13Parser;
import net.sourceforge.pmd.lang.java.Java14Parser;
import net.sourceforge.pmd.lang.java.Java15Parser;
import net.sourceforge.pmd.lang.java.Java18Parser;
import net.sourceforge.pmd.lang.java.Java16Parser;
import net.sourceforge.pmd.lang.java.Java17Parser;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import org.incava.analysis.Report;
import org.incava.diffj.compunit.CompilationUnit;
import org.incava.java.Java;

public class Parser {
    public ASTCompilationUnit parse(String label, String contents, String version) throws Exception {
        net.sourceforge.pmd.lang.Parser p = getParser(version);
        Reader reader = new StringReader(contents);
        return (ASTCompilationUnit)p.parse(label, reader);
    }

    public net.sourceforge.pmd.lang.Parser getParser(String version) {
        ParserOptions opts = new ParserOptions();
        switch (version) {
            case Java.SOURCE_1_3: return new Java13Parser(opts);
            case Java.SOURCE_1_4: return new Java14Parser(opts);
            case Java.SOURCE_1_5: return new Java15Parser(opts);
            case Java.SOURCE_1_6: return new Java16Parser(opts);
            case Java.SOURCE_1_7: return new Java17Parser(opts);
            case Java.SOURCE_1_8: return new Java18Parser(opts);
            default: throw new RuntimeException("version '" + version + "' is not 1.3 through 1.8");
        }
    }
}    
