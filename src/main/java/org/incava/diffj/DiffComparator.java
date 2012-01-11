package org.incava.diffj;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.analysis.Report;
import org.incava.pmdx.SimpleNodeUtil;

public class DiffComparator {
    private final Report report;    
    private final Collection<FileDiff> fileDiffs;

    public DiffComparator(Report report) {
        this.report = report;
        this.fileDiffs = report.getDifferences();
    }

    public DiffComparator(Collection<FileDiff> refs) {
        this.report = null;
        this.fileDiffs = refs;
    }

    public DiffComparator() {
        this(new ArrayList<FileDiff>());
    }

    public Collection<FileDiff> getFileDiffs() {
        return fileDiffs;
    }

    // -------------------------------------------------------

    protected void add(FileDiff ref) {
        tr.Ace.stack("ref: " + ref);
        fileDiffs.add(ref);
    }

    protected Object[] toParameters(Token a, Token b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(a.image);
        }
        if (b != null) {
            params.add(b.image);
        }
        return params.toArray(new Object[params.size()]);
    }

    protected Object[] toParameters(SimpleNode a, SimpleNode b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(SimpleNodeUtil.toString(a));
        }
        if (b != null) {
            params.add(SimpleNodeUtil.toString(b));
        }
        return params.toArray(new Object[params.size()]);
    }

    // -------------------------------------------------------
    // changed
    // -------------------------------------------------------

    protected void changed(Token from, Token to, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffChange(str, from, to));
    }

    protected void changed(Token from, Token to, String msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    protected void changed(SimpleNode from, SimpleNode to, String msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    protected void changed(SimpleNode from, SimpleNode to, String msg, Object ... params) {
        changed(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    protected void changed(Token fromStart, Token fromEnd, Token toStart, Token toEnd, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffChange(str, fromStart, fromEnd, toStart, toEnd));
    }

    protected void changed(SimpleNode from, Token to, String msg, Object ... params) {
        changed(from.getFirstToken(), from.getLastToken(), to, to, msg, params);
    }

    protected void changed(Token from, SimpleNode to, String msg, Object ... params) {
        changed(from, from, to.getFirstToken(), to.getLastToken(), msg, params);
    }

    // -------------------------------------------------------
    // deleted
    // -------------------------------------------------------

    protected void deleted(Token from, Token to, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffDelete(str, from, to));
    }

    protected void deleted(Token from, Token to, String msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    protected void deleted(SimpleNode from, SimpleNode to, String msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    protected void deleted(SimpleNode from, SimpleNode to, String msg, Object ... params) {
        deleted(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    protected void deleted(Token fromStart, Token fromEnd, Token toStart, Token toEnd, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffDelete(str, fromStart, fromEnd, toStart, toEnd));
    }

    protected void deleted(SimpleNode from, Token to, String msg, Object ... params) {
        deleted(from.getFirstToken(), from.getLastToken(), to, to, msg, params);
    }

    protected void deleted(Token from, SimpleNode to, String msg, Object ... params) {
        deleted(from, from, to.getFirstToken(), to.getLastToken(), msg, params);
    }

    // -------------------------------------------------------
    // added
    // -------------------------------------------------------

    protected void added(Token from, Token to, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffAdd(str, from, to));
    }

    protected void added(Token from, Token to, String msg) {
        added(from, to, msg, toParameters(null, to));
    }

    protected void added(SimpleNode from, SimpleNode to, String msg) {
        added(from, to, msg, toParameters(null, to));
    }

    protected void added(SimpleNode from, SimpleNode to, String msg, Object ... params) {
        added(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    protected void added(Token fromStart, Token fromEnd, Token toStart, Token toEnd, String msg, Object ... params) {
        String str = MessageFormat.format(msg, params);
        add(new FileDiffAdd(str, fromStart, fromEnd, toStart, toEnd));
    }

    protected void added(SimpleNode from, Token to, String msg, Object ... params) {
        added(from.getFirstToken(), from.getLastToken(), to, to, msg, params);
    }

    protected void added(Token from, SimpleNode to, String msg, Object ... params) {
        added(from, from, to.getFirstToken(), to.getLastToken(), msg, params);
    }
}
