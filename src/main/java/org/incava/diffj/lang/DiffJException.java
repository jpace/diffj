package org.incava.diffj.lang;

public class DiffJException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public DiffJException(Exception e) {
        super(e);
    }

    public DiffJException(String msg, Exception e) {
        super(msg, e);
    }

    public DiffJException(String msg) {
        super(msg);
    }
}
