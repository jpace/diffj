package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.analysis.TokenUtil;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.Node;

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

    public String toString() {
        return fileDiffs.toString();
    }

    public FileDiffs getFileDiffs() {
        return fileDiffs;
    }

    public void add(FileDiff fdiff) {
        fileDiffs.add(fdiff);
    }

    public Object[] toParameters(Token a, Token b) {
        return DiffParameters.toParameters(a, b);
    }

    public Object[] toParameters(AbstractJavaNode a, AbstractJavaNode b) {
        return DiffParameters.toParameters(a, b);
    }

    public LocationRange toRange(Token from, Token to) {
        return TokenUtil.toLocationRange(from, to);
    }
    
    // -------------------------------------------------------
    // changed
    // -------------------------------------------------------

    public void changed(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        add(new FileDiffChange(toRange(fromStart, fromEnd), toRange(toStart, toEnd), msg, params));
    }

    public void changed(Element from, Element to, Message msg, Object ... params) {
        changed(from.getNode(), to.getNode(), msg, params);
    }

    public void changed(Token from, Token to, Message msg, Object ... params) {
        changed(from, from, to, to, msg, params);
    }

    public void changed(AbstractJavaNode from, AbstractJavaNode to, Message msg, Object ... params) {
        changed(Node.of(from).getFirstToken(), Node.of(from).getLastToken(), Node.of(to).getFirstToken(), Node.of(to).getLastToken(), msg, params);
    }

    public void changed(AbstractJavaNode from, Token to, Message msg, Object ... params) {
        changed(Node.of(from).getFirstToken(), Node.of(from).getLastToken(), to, to, msg, params);
    }

    public void changed(Token from, AbstractJavaNode to, Message msg, Object ... params) {
        changed(from, from, Node.of(to).getFirstToken(), Node.of(to).getLastToken(), msg, params);
    }

    public void changed(Token from, Token to, Message msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    public void changed(AbstractJavaNode from, AbstractJavaNode to, Message msg) {
        changed(from, to, msg, toParameters(from, to));
    }

    // -------------------------------------------------------
    // deleted
    // -------------------------------------------------------

    public void deleted(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        add(new FileDiffDelete(toRange(fromStart, fromEnd), toRange(toStart, toEnd), msg, params));
    }

    public void deleted(Token from, Token to, Message msg, Object ... params) {
        deleted(from, from, to, to, msg, params);
    }

    public void deleted(AbstractJavaNode from, AbstractJavaNode to, Message msg, Object ... params) {
        deleted(Node.of(from).getFirstToken(), Node.of(from).getLastToken(), Node.of(to).getFirstToken(), Node.of(to).getLastToken(), msg, params);
    }

    public void deleted(Token from, Token to, Message msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    public void deleted(AbstractJavaNode from, AbstractJavaNode to, Message msg) {
        deleted(from, to, msg, toParameters(from, null));
    }

    // -------------------------------------------------------
    // added
    // -------------------------------------------------------

    public void added(Token fromStart, Token fromEnd, Token toStart, Token toEnd, Message msg, Object ... params) {
        add(new FileDiffAdd(toRange(fromStart, fromEnd), toRange(toStart, toEnd), msg, params));
    }

    public void added(Token from, Token to, Message msg, Object ... params) {
        added(from, from, to, to, msg, params);
    }

    public void added(Token from, Token to, Message msg) {
        added(from, to, msg, toParameters(null, to));
    }

    public void added(AbstractJavaNode from, AbstractJavaNode to, Message msg, Object ... params) {
        added(Node.of(from).getFirstToken(), Node.of(from).getLastToken(), Node.of(to).getFirstToken(), Node.of(to).getLastToken(), msg, params);
    }

    public void added(AbstractJavaNode from, AbstractJavaNode to, Message msg) {
        added(from, to, msg, toParameters(null, to));
    }
}
