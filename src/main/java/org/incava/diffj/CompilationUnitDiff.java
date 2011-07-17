package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiff;
import org.incava.analysis.Report;
import org.incava.ijdk.util.TimedEvent;
import org.incava.pmd.*;


public class CompilationUnitDiff extends DiffComparator {

    private final Report report;
    
    private final boolean flush;
    
    public CompilationUnitDiff(Report report, boolean flush) {
        super(report);

        this.report = report;
        this.flush = flush;
    }

    public CompilationUnitDiff(Collection<FileDiff> diffs) {
        super(diffs);

        this.report = null;
        this.flush = false;
    }

    public CompilationUnitDiff() {
        this.report = null;
        this.flush = false;
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        Collection<FileDiff> refs = getFileDiffs();
        tr.Ace.log("refs", refs);
        
        if (a != null && b != null) {
            PackageDiff pd = new PackageDiff(refs);
            pd.compare(a, b);
            
            if (flush && report != null) {
                report.flush();
            }
            
            ImportsDiff id = new ImportsDiff(refs);
            id.compare(a, b);
            
            if (flush && report != null) {
                report.flush();
            }

            // TimedEvent typetime = new TimedEvent("type");
            TypesDiff td = new TypesDiff(refs);
            td.compare(a, b);
            // typetime.close();
        }
    }
}
