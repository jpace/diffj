package org.incava.diffj.element;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.analysis.TokenUtil;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.SimpleNodeUtil;

public class Differences {
    private final Report report;    
    private final FileDiffs fileDiffs;

    public Differences(Report report) {
        this.report = report;
        this.fileDiffs = report.getDifferences();
    }

    public Differences(FileDiffs diffs) {
        this.report = null;
        this.fileDiffs = diffs;
    }

    public Differences() {
        this(new FileDiffs());
    }

    public FileDiffs getFileDiffs() {
        return fileDiffs;
    }

    public void add(FileDiff fdiff) {
        tr.Ace.stack("fdiff", fdiff);
        fileDiffs.add(fdiff);
    }

    public Object[] toParameters(Token a, Token b) {
        List<Object> params = new ArrayList<Object>();
        if (a != null) {
            params.add(a.image);
        }
        if (b != null) {
            params.add(b.image);
        }
        return params.toArray(new Object[params.size()]);
    }

    public Object[] toParameters(SimpleNode a, SimpleNode b) {
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

    public void changed(Element from, Element to, Message msg, Object ... params) {
        changed(from.getNode(), to.getNode(), msg, params);
    }

    public void changed(Token from, Token to, Message msg, Object ... params) {
        changed(from, from, to, to, msg, params);
    }

    public void changed(SimpleNode from, SimpleNode to, Message msg, Object ... params) {
        changed(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    public void changed(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        String str = msg.format(params);
        add(new FileDiffChange(str, TokenUtil.toLocationRange(fromStart, fromEnd), TokenUtil.toLocationRange(toStart, toEnd)));
    }

    public void changed(SimpleNode from, Token to, Message msg, Object ... params) {
        changed(from.getFirstToken(), from.getLastToken(), to, to, msg, params);
    }

    public void changed(Token from, SimpleNode to, Message msg, Object ... params) {
        changed(from, from, to.getFirstToken(), to.getLastToken(), msg, params);
    }

    public void changed(Token from, Token to, Message msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    public void changed(SimpleNode from, SimpleNode to, Message msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    // -------------------------------------------------------
    // deleted
    // -------------------------------------------------------

    public void deleted(Token from, Token to, Message msg, Object ... params) {
        deleted(from, from, to, to, msg, params);
    }

    public void deleted(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        String str = msg.format(params);
        add(new FileDiffDelete(str, TokenUtil.toLocationRange(fromStart, fromEnd), TokenUtil.toLocationRange(toStart, toEnd)));
    }

    public void deleted(SimpleNode from, SimpleNode to, Message msg, Object ... params) {
        deleted(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    public void deleted(Token from, Token to, Message msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    public void deleted(SimpleNode from, SimpleNode to, Message msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    // -------------------------------------------------------
    // added
    // -------------------------------------------------------

    public void added(Token from, Token to, Message msg, Object ... params) {
        String str = msg.format(params);
        add(new FileDiffAdd(str, from, to));
    }

    public void added(Token from, Token to, Message msg) {
        added(from, to, msg, toParameters(null, to));
    }

    public void added(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        String str = msg.format(params);
        add(new FileDiffAdd(str, fromStart, fromEnd, toStart, toEnd));
    }

    public void added(SimpleNode from, SimpleNode to, Message msg, Object ... params) {
        added(from.getFirstToken(), from.getLastToken(), to.getFirstToken(), to.getLastToken(), msg, params);
    }

    public void added(SimpleNode from, SimpleNode to, Message msg) {
        added(from, to, msg, toParameters(null, to));
    }
}
