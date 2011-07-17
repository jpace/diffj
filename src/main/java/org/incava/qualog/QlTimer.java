package org.incava.qualog;

import java.util.*;


public class QlTimer {
    private final List<QlTimedPeriod> periods;

    public QlTimer() {
        periods = new ArrayList<QlTimedPeriod>();
    }

    public boolean start() {
        return start(null);
    }

    public boolean end() {
        return end(null);        
    }

    public boolean start(String msg) {
        StackTraceElement ste = getFrame();

        String className  = ste.getClassName();
        String methodName = ste.getMethodName();
        int    lineNumber = ste.getLineNumber();
        String fileName   = ste.getFileName();

        QlTimedPeriod qtp = new QlTimedPeriod(fileName, className, methodName, lineNumber, msg);
        periods.add(qtp);

        return true;
    }

    public boolean end(String msg) {
        long endTime = System.currentTimeMillis();

        StackTraceElement ste = getFrame();

        // System.out.println("ste: " + ste);

        String className     = ste.getClassName();
        String methodName    = ste.getMethodName();
        int    lineNumber    = ste.getLineNumber();
        String fileName      = ste.getFileName();
        int    bestMatchIdx  = -1;
        int    bestMatchness = -1;
        
        Iterator<QlTimedPeriod> pit = periods.iterator();
        for (int idx = 0; pit.hasNext(); ++idx) {
            QlTimedPeriod qtp       = pit.next();
            int           matchness = 0;
            
            matchness += qtp.getMessage() != null && msg.equals(qtp.getMessage()) ? 1 : 0;
            matchness += className.equals(qtp.getClassName())   ? 1 : 0;
            matchness += fileName.equals(qtp.getFileName())     ? 1 : 0;
            matchness += methodName.equals(qtp.getMethodName()) ? 1 : 0;

            // System.out.println("matchness: " + matchness);
            if (matchness >= bestMatchness) {
                bestMatchness = matchness;
                bestMatchIdx  = idx;
            }
        }

        // System.out.println("best matchness: " + bestMatchness);
        // System.out.println("best match idx: " + bestMatchIdx);

        if (bestMatchIdx >= 0) {
            QlTimedPeriod qtp     = periods.remove(bestMatchIdx);
            long          elapsed = endTime - qtp.getStartTime();
            StringBuffer  buf     = new StringBuffer();

            buf.append(format(elapsed));
            buf.append("; ");
                
            if (msg != null) {
                buf.append(msg);
                buf.append("; ");
            }            
            
            buf.append("from: [");
            buf.append(fileName);
            buf.append(":");
            buf.append(Integer.toString(lineNumber));
            buf.append("]");
            buf.append(" ");

            buf.append("{");
            buf.append(className);
            buf.append("#");
            buf.append(methodName);
            buf.append("}");

            Qualog.log(buf.toString());
        }
        else {
            System.err.println("ERROR no matching start!");
        }

        return true;
    }

    protected StackTraceElement getFrame() {
        StackTraceElement[] stack = (new Exception("")).getStackTrace();
        int                 stIdx = Qualog.findStackStart(stack);
        StackTraceElement   ste   = stack[stIdx];

        return ste;
    }

    public String format(long duration) {
        StringBuffer buf = new StringBuffer();
        if (duration < 10000) {
            buf.append(Long.toString(duration));
            buf.append(" ms");
        }
        else if (duration < 100000) {
            double nSecs = duration / 1000.0;
            buf.append(Double.toString(nSecs));
            buf.append(" sec");
        }
        else if (duration < 1000000) {
            double nMin = Math.floor(duration / (60 * 1000.0));
            double nSec = (duration - 60.0 * nMin) / 1000.0;
            buf.append(Double.toString(nMin));
            buf.append(":");
            buf.append(Double.toString(nSec));
        }
        else {
            // convert to HH:MM:SS, etc.
            buf.append(Long.toString(duration));
        }
        return buf.toString();
    }

}
