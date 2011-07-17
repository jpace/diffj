package org.incava.qualog;

import java.io.*;
import java.util.*;


/**
 * <p>Writes the logging output, applying filters and decorations. The
 * <code>Qualog</code> class offers a much cleaner and more thorough interface
 * than this class.</p>
 *
 * @see org.incava.qualog.Qualog
 */
public class QlWriter {
    public static final int NO_OUTPUT = 0;

    public static final int QUIET = 1;
    
    public static final int VERBOSE = 2;
    
    public int fileWidth = 25;

    public boolean columns = true;

    public int lineWidth = 5;

    public int functionWidth = 25;

    public int classWidth = 35;

    public boolean showFiles = true;

    public boolean showClasses = true;

    // this writes to stdout even in Gradle and Ant, which redirect stdout.

    public PrintWriter out = new PrintWriter(new PrintStream(new FileOutputStream(FileDescriptor.out)), true);
    
    public List<String> packagesSkipped = new ArrayList<String>(Arrays.asList(
                                                                    new String[] {
                                                                        "org.incava.qualog",
                                                                    }));
    
    public List<String> classesSkipped = new ArrayList<String>(Arrays.asList(
                                                                   new String[] {
                                                                       "tr.Ace"
                                                                   }));
    
    public List<String> methodsSkipped = new ArrayList<String>(Arrays.asList(
                                                                   new String[] {
                                                                   }));
    
    private int outputType = NO_OUTPUT;

    private Map<String, ANSIColor> packageColors = new HashMap<String, ANSIColor>();

    private Map<String, ANSIColor> classColors = new HashMap<String, ANSIColor>();

    private Map<String, ANSIColor> methodColors = new HashMap<String, ANSIColor>();

    private Map<String, ANSIColor> fileColors = new HashMap<String, ANSIColor>();

    private StackTraceElement prevStackElement = null;
    
    private Thread prevThread = null;

    private String prevDisplayedClass = null;

    private String prevDisplayedMethod = null;

    private QlLevel level = Qualog.LEVEL9;

    private List<QlFilter> filters = new ArrayList<QlFilter>();

    private boolean useColor = true;

    /**
     * Adds a filter to be applied for output.
     *
     * @see org.incava.qualog.QlFilter
     */
    public void addFilter(QlFilter filter) {
        filters.add(filter);
    }

    public void setDisabled(Class cls) {
        addFilter(new QlClassFilter(cls, null));
    }

    public void setClassColor(String className, ANSIColor color) {
        classColors.put(className, color);
    }

    public void setPackageColor(String pkg, ANSIColor color) {
    }

    public void setMethodColor(String className, String methodName, ANSIColor color) {
        methodColors.put(className + "#" + methodName, color);
    }

    public void clearClassColor(String className) {
        classColors.remove(className);
    }

    public void setFileColor(String fileName, ANSIColor color) {
        fileColors.put(fileName, color);
    }

    public void set(boolean columns, int fileWidth, int lineWidth, int classWidth, int functionWidth) {
        this.columns = columns;
        this.fileWidth = fileWidth;
        this.lineWidth = lineWidth;
        this.classWidth = classWidth;
        this.functionWidth = functionWidth;
    }

    /**
     * Sets the output type and level. Either verbose or quiet can be enabled.
     */
    public void setOutput(int type, QlLevel level) {
        this.outputType = type;
        this.level      = level;
    }

    public boolean verbose() {
        return outputType == VERBOSE;
    }

    public void setColumns(boolean cols) {
        columns = cols;
    }

    public void addClassSkipped(Class cls) {
        addClassSkipped(cls.getName());
    }
    
    public void addClassSkipped(String clsName) {
        classesSkipped.add(clsName);
    }

    /**
     * Resets parameters to their defaults.
     */
    public void clear() {
        packageColors = new HashMap<String, ANSIColor>();
        classColors = new HashMap<String, ANSIColor>();
        methodColors = new HashMap<String, ANSIColor>();
        fileColors = new HashMap<String, ANSIColor>();
        prevStackElement = null;
        prevThread = null;
        prevDisplayedClass = null;
        prevDisplayedMethod = null;
        level = Qualog.LEVEL9;
        filters = new ArrayList<QlFilter>();
    }

    public void reset() {
        prevThread       = Thread.currentThread();
        prevStackElement = null;
    }

    public boolean stack(QlLevel level, 
                         ANSIColor[] msgColors,
                         String name,
                         Object obj,
                         ANSIColor fileColor,
                         ANSIColor classColor,
                         ANSIColor methodColor,
                         int numFrames) {
        if (isLoggable(level)) {
            String nm = name == null ? "" : name;
        
            if (obj == null) {
                String msg = nm + ": " + "null";
                return stack(level, msgColors, msg, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj instanceof Collection) {
                Collection c = (Collection)obj;
                return QlCollection.stack(level, msgColors, nm, c, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj instanceof Iterator) {
                Iterator<?> it = (Iterator<?>)obj;
                return QlIterator.stack(level, msgColors, nm, it, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj instanceof Enumeration) {
                Enumeration<?> en = (Enumeration<?>)obj;
                return QlEnumeration.stack(level, msgColors, nm, en, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj instanceof Object[]) {
                Object[] ary = (Object[])obj;
                return QlObjectArray.stack(level, msgColors, nm, ary, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj instanceof Map) {
                Map m = (Map)obj;
                return QlMap.stack(level, msgColors, nm, m, fileColor, classColor, methodColor, numFrames);
            }
            else if (obj.getClass().isArray()) {
                String[] strs = null;
                if (obj instanceof byte[]) {
                    byte[] ary = (byte[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                }
                else if (obj instanceof char[]) {
                    char[] ary = (char[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                }
                else if (obj instanceof double[]) {
                    double[] ary = (double[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                } 
                else if (obj instanceof float[]) {
                    float[] ary = (float[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                }
                else if (obj instanceof int[]) {
                    int[] ary = (int[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                }
                else if (obj instanceof long[]) {
                    long[] ary = (long[])obj;
                    strs = new String[ary.length];
                    for (int ai = 0; ai < ary.length; ++ai) {
                        strs[ai] = String.valueOf(ary[ai]);
                    }
                }

                return QlObjectArray.stack(level, msgColors, nm, strs, fileColor, classColor, methodColor, numFrames);
            }
            else {
                String msg = nm + ": " + objectToString(obj);
                return stack(level, msgColors, msg, fileColor, classColor, methodColor, numFrames);
            }
        }
        else {
            return true;
        }
    }

    public boolean isSkipped(StackTraceElement ste) {
        String className = ste.getClassName();
        if (classesSkipped.contains(className) || methodsSkipped.contains(ste.getMethodName())) {
            return true;
        }
        else {
            for (String pkgName : packagesSkipped) {
                if (className.startsWith(pkgName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLoggable(QlLevel level) {
        return outputType != NO_OUTPUT && this.level != null && this.level.compareTo(level) >= 0;
    }


    /**
     * Returns the index in the stack where logging (stacks) should be
     * displayed. Returns -1 if the end of the stack is reached and no logging
     * should occur.
     */
    public synchronized int findStackStart(StackTraceElement[] stack) {
        for (int fi = 0; fi < stack.length; ++fi) {
            if (!isSkipped(stack[fi])) {
                return fi;
            }
        }
        
        return stack.length;
    }

    public synchronized boolean stack(QlLevel lvl,
                                      ANSIColor[] msgColor,
                                      String msg,
                                      ANSIColor fileColor,
                                      ANSIColor classColor,
                                      ANSIColor methodColor,
                                      int numFrames) {
        if (outputType != NO_OUTPUT && level != null && level.compareTo(lvl) >= 0) {

            if (outputType == QUIET) {
                numFrames = 1;
            }

            StackTraceElement[] stack = getStack(numFrames);

            // when we're switching threads, reset to a null state.
            if (!Thread.currentThread().equals(prevThread)) {
                reset();
            }

            int fi = findStackStart(stack);

            for (int framesShown = 0; fi < stack.length && framesShown < numFrames; ++fi, ++framesShown) {
                StackTraceElement stackElement = stack[fi];
                String            className    = stackElement.getClassName();
                String            methodName   = stackElement.getMethodName();
                boolean           isLoggable   = true;
                
                if (framesShown == 0) {
                    for (QlFilter filter : filters) {
                        int    lineNum  = stackElement.getLineNumber();
                        String fileName = stackElement.getFileName();
                            
                        if (filter.isMatch(fileName, lineNum, className, methodName)) {
                            QlLevel flevel = filter.getLevel();
                            isLoggable = flevel != null && level.compareTo(flevel) >= 0;
                        }
                    }
                }

                if (!isLoggable) {
                    return true;
                }

                StringBuffer buf = new StringBuffer();

                if (outputType == VERBOSE) {
                    if (showFiles) {
                        outputFileName(buf, fileColor, stackElement);
                    }
                    if (showClasses) {
                        outputClassAndMethod(buf, classColor, methodColor, stackElement);
                    }
                }
                outputMessage(buf, framesShown, msgColor, msg, stackElement);

                out.println(buf.toString());

                // System.err.println("buf: " + buf.toString());

                prevStackElement = stackElement;
            }
        }
        return true;
    }

    void setUseColor(boolean useColor) {
        this.useColor = useColor;
    }

    protected void outputFileName(StringBuffer buf, ANSIColor fileColor, StackTraceElement stackElement) {
        String fileName = stackElement.getFileName();
        
        buf.append("[");
        if (fileName == null) {
            fileName = "";
        }

        if (prevStackElement != null && 
            prevStackElement.getFileName() != null && 
            prevStackElement.getFileName().equals(fileName)) {
            
            int width = columns ? Math.min(fileWidth, fileName.length()) : fileName.length();
            fileName = repeat(width, ' ');
        }

        String lnStr = stackElement.getLineNumber() >= 0 ? String.valueOf(stackElement.getLineNumber()) : "";

        ANSIColor col = fileColor;
        if (col == null) {
            col = fileColors.get(fileName);
        }

        if (columns) {
            if (col == null) {
                appendPadded(buf, fileName, fileWidth);
                buf.append(' ');
                buf.append(repeat(lineWidth - lnStr.length(), ' ')).append(lnStr);
            }
            else {
                buf.append(col);
                buf.append(fileName);
                buf.append(Qualog.NONE);
                repeat(buf, fileWidth - fileName.length(), ' ');
                repeat(buf, 1 + lineWidth - lnStr.length(), ' ');
                buf.append(col).append(lnStr).append(Qualog.NONE);
            }
        }
        else if (col == null) {
            appendPadded(buf, fileName + ":" + lnStr, fileWidth);
        }
        else {
            buf.append(col);
            buf.append(fileName);
            buf.append(':');
            buf.append(lnStr);
            buf.append(Qualog.NONE);
            repeat(buf, fileWidth - fileName.length() - 1 - lnStr.length(), ' ');
        }

        buf.append("] ");
    }

    protected void outputClassAndMethod(StringBuffer buf,
                                        ANSIColor classColor,
                                        ANSIColor methodColor,
                                        StackTraceElement stackElement) {
        buf.append("{");

        String className = stackElement.getClassName();
        
        if (classColor == null) {
            classColor = classColors.get(className);
        }
        
        boolean sameClass = prevStackElement != null && prevStackElement.getClassName().equals(className);
        if (sameClass) {
            className = repeat(prevDisplayedClass.length(), ' ');
            classColor = null;
        }
        else if (className != null && (className.startsWith("org.") || className.startsWith("com."))) {
            className = "..." + className.substring(className.indexOf('.', 5) + 1);
        }

        int totalWidth = classWidth + 1 + functionWidth;

        int classPadding = 0;
        if (className.length() > classWidth) {
            if (classWidth > 0) {
                className = className.substring(0, classWidth - 1) + '-';
            }
            else {
                className = "";
            }
        }
        else {
            classPadding = classWidth - className.length();
        }

        if (classColor != null) {
            buf.append(classColor);
        }
        buf.append(className);
        if (classColor != null) {
            buf.append(Qualog.NONE);
        }

        if (columns) {
            repeat(buf, classPadding, ' ');
        }

        prevDisplayedClass = className;

        buf.append('#');
        
        String methodName = stackElement.getMethodName();

        if (methodColor == null) {
            methodColor = methodColors.get(methodName);
        }
        
        if (sameClass && prevStackElement != null && prevStackElement.getMethodName().equals(methodName)) {
            methodName = repeat(prevDisplayedMethod.length(), ' ');
            methodColor = null;
        }

        int methodPadding = 0;
        if (methodName.length() > functionWidth) {
            methodName = methodName.substring(0, functionWidth - 1) + '-';
        }
        else {
            methodPadding = functionWidth - methodName.length();
        }

        if (methodColor != null) {
            buf.append(methodColor);
        }
        buf.append(methodName);
        if (methodColor != null) {
            buf.append(Qualog.NONE);
        }

        if (!columns) {
            repeat(buf, classPadding, ' ');
        }
        repeat(buf, methodPadding, ' ');

        prevDisplayedMethod = methodName;

        buf.append("} ");
    }

    protected void outputMessage(StringBuffer buf,
                                 int framesShown,
                                 ANSIColor[] msgColor,
                                 String msg,
                                 StackTraceElement stackElement) {
        // remove ending EOLN
        if (framesShown > 0) {
            msg = "\"\"";
        }
        else {
            while (msg.length() > 0 && "\r\n".indexOf(msg.charAt(msg.length() - 1)) != -1) {
                msg = msg.substring(0, msg.length() - 1);
            }
            if (useColor) {
                boolean hasColor = false;
                if (msgColor == null || (msgColor.length > 0 && msgColor[0] == null)) {
                    ANSIColor col = null;
                    col = methodColors.get(stackElement.getClassName() + "#" + stackElement.getMethodName());
                    if (col == null) {
                        col = classColors.get(stackElement.getClassName());
                        if (col == null) {
                            col = fileColors.get(stackElement.getFileName());
                        }
                    }
                    if (col != null) {
                        msg = col + msg;
                        hasColor = true;
                    }
                }
                else {
                    for (int i = 0; i < msgColor.length; ++i) {
                        if (msgColor[i] != null) {
                            msg = msgColor[i] + msg;
                            hasColor = true;
                        }
                    }
                }

                if (hasColor) {
                    msg += Qualog.NONE;
                }
            }
        }

        buf.append(msg);
    }
    
    protected StackTraceElement[] getStack(int depth) {
        return (new Exception("")).getStackTrace();
    }

    protected String repeat(int len, char ch) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            buf.append(ch);
        }
        return buf.toString();
    }

    protected StringBuffer repeat(StringBuffer buf, int len, char ch) {
        for (int i = 0; i < len; ++i) {
            buf.append(ch);
        }
        return buf;
    }

    protected void appendPadded(StringBuffer buf, String str, int maxSize) {
        if (str.length() > maxSize) {
            buf.append(str.substring(0, maxSize - 1)).append("-");
        }
        else {
            buf.append(str);
            repeat(buf, maxSize - str.length(), ' ');
        }
    }

    protected String objectToString(Object obj) {
        String str = null;
        if (obj == null) {
            str = "null";
        }
        else {
            
            Class<?>[] undecorated = new Class<?>[] {
                String.class,
                Number.class,
                Character.class,
                Boolean.class
            };

            Class<?> cls = obj.getClass();

            for (int ui = 0; ui < undecorated.length; ++ui) {
                if (undecorated[ui].isAssignableFrom(cls)) {
                    str = obj.toString();
                    break;
                }
            }

            if (str == null) {
                str = obj.toString() + " (" + obj.getClass() + ") #" + Integer.toHexString(obj.hashCode());
            }
        }
        return str;
    }
        

}
