package org.incava.qualog;

import java.util.*;


public class QlTimedPeriod {
    String _fileName;

    String _className;

    String _methodName;
    
    int _lineNumber;

    String _message;

    long _start;

    public QlTimedPeriod(String fileName, String className, String methodName, int lineNumber, String message) {
        _fileName   = fileName;
        _className  = className;
        _methodName = methodName;
        _lineNumber = lineNumber;
        _message    = message;
        _start      = System.currentTimeMillis();
    }

    public String getFileName() {
        return _fileName;
    }

    public String getClassName() {
        return _className;
    }
    
    public String getMethodName() {
        return _methodName;
    }

    public int getLineNumber() {
        return _lineNumber;
    }

    public String getMessage() {
        return _message;
    }

    public long getStartTime() {
        return _start;
    }

}

