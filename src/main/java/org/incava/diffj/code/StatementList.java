package org.incava.diffj.code;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Differ;
import org.incava.ijdk.util.diff.Difference;
import org.incava.pmdx.SimpleNodeUtil;

public class StatementList {
    private final List<Statement> statements;
    
    public StatementList(List<Statement> statements) {
        this.statements = statements;
    }
}
