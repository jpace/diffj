package org.incava.qualog;

import java.io.*;
import java.util.*;


/**
 * <p>Provides quasi-logging support, more akin to debugging/development output
 * and trace statements than logging per se. Supports both tabular and
 * non-tabular output formats, the former being with the files, line numbers,
 * classes, and methods being arranged so that they line up vertically. That
 * format, I've found, is better for larger projects (500M+ LOC), in which class
 * names and package hierarchies tend to be larger. The non-tabular format seems
 * better for smaller projects.</p>
 *
 * <p>Colors can be enabled and disabled, and associated with classes, methods,
 * files, and levels. They are designed to work on terminals that support ANSI
 * escape codes. On platforms without this -- e.g., Windows -- colorization is
 * disabled.</p>
 *
 * <p>Unlike real logging mechanisms, there is no support for log rotations. I
 * recommend log4j for that. This package is mainly for programmers who want
 * trace statements from a Java program. See Kernighan and Pike for a defense of
 * those of us who develop and debug programs mainly relying on the print
 * statement.</p>
 *
 * <p>There is a serious performance hit to using this package, since each
 * output statement results in an exception being created.</p>
 */
public class Qualog {
    /**
     * The version of the Qualog module.
     */
    public final static String VERSION = "1.0.2";
    
    /**
     * An array denoting no colors.
     */
    public final static ANSIColor[] NO_COLORS = null;

    /**
     * An object denoting no color.
     */
    public final static ANSIColor NO_COLOR = null;
    
    /**
     * The code for no color applied.
     */
    public final static ANSIColor NONE = ANSIColor.NONE;

    /**
     * The code for reset of colors and decorations.
     */
    public final static ANSIColor RESET = ANSIColor.RESET;

    /**
     * The code for bold decoration.
     */
    public final static ANSIColor BOLD = ANSIColor.BOLD;

    /**
     * The code for underscore (AKA underline).
     */
    public final static ANSIColor UNDERSCORE = ANSIColor.UNDERSCORE;

    /**
     * The code for underline (AKA underscore).
     */
    public final static ANSIColor UNDERLINE = ANSIColor.UNDERLINE;

    /**
     * The code for the blink attribute.
     */
    public final static ANSIColor BLINK = ANSIColor.BLINK;

    /**
     * The code for reversed text.
     */
    public final static ANSIColor REVERSE = ANSIColor.REVERSE;

    /**
     * The code for hidden text.
     */
    public final static ANSIColor CONCEALED = ANSIColor.CONCEALED;

    /**
     * The code for black text.
     */
    public final static ANSIColor BLACK = ANSIColor.BLACK;

    /**
     * The code for red text.
     */
    public final static ANSIColor RED = ANSIColor.RED;

    /**
     * The code for green text.
     */
    public final static ANSIColor GREEN = ANSIColor.GREEN;

    /**
     * The code for yellow text.
     */
    public final static ANSIColor YELLOW = ANSIColor.YELLOW;

    /**
     * The code for blue text.
     */
    public final static ANSIColor BLUE = ANSIColor.BLUE;

    /**
     * The code for magenta text.
     */
    public final static ANSIColor MAGENTA = ANSIColor.MAGENTA;

    /**
     * The code for cyan text.
     */
    public final static ANSIColor CYAN = ANSIColor.CYAN;

    /**
     * The code for white text.
     */
    public final static ANSIColor WHITE = ANSIColor.WHITE;

    /**
     * The code for black background.
     */
    public final static ANSIColor ON_BLACK = ANSIColor.ON_BLACK;

    /**
     * The code for red background.
     */
    public final static ANSIColor ON_RED = ANSIColor.ON_RED;

    /**
     * The code for green background.
     */
    public final static ANSIColor ON_GREEN = ANSIColor.ON_GREEN;

    /**
     * The code for yellow background.
     */
    public final static ANSIColor ON_YELLOW = ANSIColor.ON_YELLOW;

    /**
     * The code for blue background.
     */
    public final static ANSIColor ON_BLUE = ANSIColor.ON_BLUE;

    /**
     * The code for magenta background.
     */
    public final static ANSIColor ON_MAGENTA = ANSIColor.ON_MAGENTA;

    /**
     * The code for cyan background.
     */
    public final static ANSIColor ON_CYAN = ANSIColor.ON_CYAN;

    /**
     * The code for white background.
     */
    public final static ANSIColor ON_WHITE = ANSIColor.ON_WHITE;
    
    public final static String CLASS_WIDTH_PROPERTY_KEY = "qualog.classwidth";
    public final static String COLUMNAR_PROPERTY_KEY = "qualog.columnar";
    public final static String FILE_WIDTH_PROPERTY_KEY = "qualog.filewidth";
    public final static String LEVEL_PROPERTY_KEY = "qualog.level";
    public final static String LINE_WIDTH_PROPERTY_KEY = "qualog.linewidth";
    public final static String METHOD_WIDTH_PROPERTY_KEY = "qualog.methodwidth";
    public final static String SHOW_CLASSES_PROPERTY_KEY = "qualog.showclasses";
    public final static String SHOW_FILES_PROPERTY_KEY = "qualog.showfiles";
    public final static String VERBOSE_PROPERTY_KEY = "qualog.verbose";
    
    public final static QlLevel LEVEL0 = new QlLevel(0);
    public final static QlLevel LEVEL1 = new QlLevel(1);
    public final static QlLevel LEVEL2 = new QlLevel(2);
    public final static QlLevel LEVEL3 = new QlLevel(3);
    public final static QlLevel LEVEL4 = new QlLevel(4);
    public final static QlLevel LEVEL5 = new QlLevel(5);
    public final static QlLevel LEVEL6 = new QlLevel(6);
    public final static QlLevel LEVEL7 = new QlLevel(7);
    public final static QlLevel LEVEL8 = new QlLevel(8);
    public final static QlLevel LEVEL9 = new QlLevel(9);

    public static final int NO_OUTPUT = QlWriter.NO_OUTPUT;

    public static final int QUIET = QlWriter.QUIET;
    
    public static final int VERBOSE = QlWriter.VERBOSE;
    
    /**
     * The default number of stack trace elements to display in a stack.
     */
    protected static final int DEFAULT_STACK_DEPTH = 5;

    protected static QlWriter writer;

    protected static QlTimer timer;

    static {
        writer = new QlWriter();
        timer = new QlTimer();
        
        String verStr = System.getProperty(VERBOSE_PROPERTY_KEY);
        if (verStr == null) {
            verStr = System.getProperty("verbose");
        }

        if (verStr != null) {
            boolean verbose = Boolean.valueOf(verStr).booleanValue();
            QlLevel level = LEVEL5;

            String lvlStr = System.getProperty(LEVEL_PROPERTY_KEY);
            if (lvlStr != null) {
                level = new QlLevel((new Integer(lvlStr)).intValue());
            }

            if (verbose) {
                setOutput(VERBOSE, level);
                System.out.println("Qualog, version " + VERSION);
            }
        }
        
        if (System.getProperty("os.name").equals("Linux")) {
            writer.setUseColor(true);
        }
        
        String showFilesStr = System.getProperty(SHOW_FILES_PROPERTY_KEY);
        if (showFilesStr != null) {
            writer.showFiles = (new Boolean(showFilesStr)).booleanValue();
        }

        String showClassesStr = System.getProperty(SHOW_CLASSES_PROPERTY_KEY);
        if (showClassesStr != null) {
            writer.showClasses = (new Boolean(showClassesStr)).booleanValue();
        }

        String columnarStr = System.getProperty(COLUMNAR_PROPERTY_KEY);
        if (columnarStr != null) {
            writer.columns = (new Boolean(columnarStr)).booleanValue();
        }

        String fileWidthStr = System.getProperty(FILE_WIDTH_PROPERTY_KEY);
        if (fileWidthStr != null) {
            writer.fileWidth = (new Integer(fileWidthStr)).intValue();
        }

        String lineWidthStr = System.getProperty(LINE_WIDTH_PROPERTY_KEY);
        if (lineWidthStr != null) {
            writer.lineWidth = (new Integer(lineWidthStr)).intValue();
        }

        String classWidthStr = System.getProperty(CLASS_WIDTH_PROPERTY_KEY);
        if (classWidthStr != null) {
            writer.classWidth = (new Integer(classWidthStr)).intValue();
        }

        String methodWidthStr = System.getProperty(METHOD_WIDTH_PROPERTY_KEY);
        if (methodWidthStr != null) {
            writer.functionWidth = (new Integer(methodWidthStr)).intValue();
        }
    }

    public static boolean isLoggable(QlLevel level) {
        return writer.isLoggable(level);
    }

    public static void setDisabled(Class cls) {
        addFilter(new QlClassFilter(cls, null));
    }

    public static void addFilter(QlFilter filter) {
        writer.addFilter(filter);
    }

    public static void setOut(PrintWriter out) {
        writer.out = out;
    }

    public static void setFileWidth(int fileWidth) {
        writer.fileWidth = fileWidth;
    }

    public static void setClassWidth(int classWidth) {
        writer.classWidth = classWidth;
    }

    public static void setLineWidth(int lineWidth) {
        writer.lineWidth = lineWidth;
    }

    public static void setFunctionWidth(int functionWidth) {
        writer.functionWidth = functionWidth;
    }

    public static void setClassColor(String className, ANSIColor color) {
        writer.setClassColor(className, color);
    }

    public static void setClassColor(ANSIColor color) {
        StackTraceElement[] stack = getStack(3);
        String className = stack[2].getClassName();
        setClassColor(className, color);
    }

    public static void setPackageColor(ANSIColor color) {
    }

    public static void setPackageColor(String pkg, ANSIColor color) {
    }

    public static void setMethodColor(String methodName, ANSIColor color) {
        StackTraceElement[] stack = getStack(3);
        String className = stack[2].getClassName();
        writer.setMethodColor(className, methodName, color);
    }

    public static void setMethodColor(String className, String methodName, ANSIColor color) {
        writer.setMethodColor(className, methodName, color);
    }

    public static void clearClassColor(String className) {
        writer.clearClassColor(className);
    }

    public static void setFileColor(String fileName, ANSIColor color) {
        writer.setFileColor(fileName, color);
    }

    public static void setFileColor(ANSIColor color) {
        StackTraceElement[] stack = getStack(3);
        String fileName = stack[2].getFileName();
        tr.Ace.red("fileName: " + fileName);
        writer.setFileColor(fileName, color);
    }

    public static void set(boolean columns, int fileWidth, int lineWidth, int classWidth, int funcWidth) {
        writer.set(columns, fileWidth, lineWidth, classWidth, funcWidth);
    }

    public static void setVerbose(boolean verbose) {
        setOutput(VERBOSE, verbose ? LEVEL5 : null);
    }

    public static void setQuiet(boolean quiet) {
        setOutput(QUIET, LEVEL5);
    }

    public static void setOutput(int type, QlLevel level) {
        writer.setOutput(type, level);
    }

    public static void setQuiet(QlLevel level) {
        writer.setOutput(QUIET, level);
    }

    public static boolean verbose() {
        return writer.verbose();
    }

    public static void setColumns(boolean cols) {
        writer.setColumns(cols);
    }
    
    public static void addClassSkipped(Class cls) {
        writer.addClassSkipped(cls);
    }
    
    public static void addClassSkipped(String clsName) {
        writer.addClassSkipped(clsName);
    }

    public static void reset() {
        writer.reset();
    }

    public static void clear() {
        writer.clear();
    }

    public static int findStackStart(StackTraceElement[] stack) {
        return writer.findStackStart(stack);
    }

    public static boolean time(String msg) {
        return timer.start(msg);
    }

    public static boolean time() {
        return timer.start();
    }

    public static boolean start(String msg) {
        return timer.start(msg);
    }

    public static boolean start() {
        return timer.start();
    }

    public static boolean end(String msg) {
        return timer.end(msg);
    }

    public static boolean end() {
        return timer.end();
    }

    public static boolean stack(QlLevel level, 
                                ANSIColor[] msgColors,
                                String name,
                                Object obj,
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames) {
        return writer.stack(level, msgColors, name, obj, fileColor, classColor, methodColor, numFrames);
    }

    public static boolean stack(ANSIColor[] msgColors,
                                String msg, 
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames) {
        return stack(LEVEL5, msgColors, msg, fileColor, classColor, methodColor, numFrames);
    }

    public static boolean stack(QlLevel level,
                                ANSIColor msgColor,
                                String msg,
                                ANSIColor fileColor,
                                ANSIColor classColor,
                                ANSIColor methodColor,
                                int numFrames) {
        return stack(level, new ANSIColor[] { msgColor }, msg, fileColor, classColor, methodColor, numFrames);
    }

    public synchronized static boolean stack(QlLevel lvl,
                                             ANSIColor[] msgColor,
                                             String msg,
                                             ANSIColor fileColor,
                                             ANSIColor classColor,
                                             ANSIColor methodColor,
                                             int numFrames) {
        return writer.stack(lvl, msgColor, msg, fileColor, classColor, methodColor, numFrames);
    }

    /**
     * Writes an empty log message.
     */
    public static boolean log() {
        return log("");
    }

    /**
     * Writes an empty stack message.
     */
    public static boolean stack() {
        return stack("");
    }

    //--- autogenerated by makeqlog

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String msg) {
        return stack(LEVEL5, NO_COLORS, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String msg) {
        return stack(LEVEL5, new ANSIColor[] { color }, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String msg) {
        return stack(LEVEL5, colors, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String msg) {
        return stack(level, NO_COLORS, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String msg) {
        return stack(level, new ANSIColor[] { color }, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String msg) {
        return stack(level, colors, msg, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(Object obj) {
        return stack(LEVEL5, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, Object obj) {
        return stack(LEVEL5, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, Object obj) {
        return stack(level, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, Object obj) {
        return stack(level, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, Object obj) {
        return stack(level, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, Object obj) {
        return stack(LEVEL5, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, Object obj) {
        return stack(LEVEL5, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, Object obj) {
        return stack(level, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, Object obj) {
        return stack(level, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, Object obj) {
        return stack(level, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(byte b) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, byte b) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, byte b) {
        return stack(LEVEL5, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, byte b) {
        return stack(level, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, byte b) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, byte b) {
        return stack(level, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, byte b) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, byte b) {
        return stack(LEVEL5, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, byte b) {
        return stack(level, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, byte b) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, byte b) {
        return stack(level, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(char c) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, char c) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, char c) {
        return stack(LEVEL5, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, char c) {
        return stack(level, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, char c) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, char c) {
        return stack(level, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, char c) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, char c) {
        return stack(LEVEL5, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, char c) {
        return stack(level, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, char c) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, char c) {
        return stack(level, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(double d) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, double d) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, double d) {
        return stack(LEVEL5, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, double d) {
        return stack(level, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, double d) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, double d) {
        return stack(level, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, double d) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, double d) {
        return stack(LEVEL5, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, double d) {
        return stack(level, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, double d) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, double d) {
        return stack(level, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(float f) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, float f) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, float f) {
        return stack(LEVEL5, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, float f) {
        return stack(level, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, float f) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, float f) {
        return stack(level, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, float f) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, float f) {
        return stack(LEVEL5, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, float f) {
        return stack(level, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, float f) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, float f) {
        return stack(level, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(int i) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, int i) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, int i) {
        return stack(LEVEL5, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, int i) {
        return stack(level, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, int i) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, int i) {
        return stack(level, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, int i) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, int i) {
        return stack(LEVEL5, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, int i) {
        return stack(level, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, int i) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, int i) {
        return stack(level, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(long l) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, long l) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, long l) {
        return stack(LEVEL5, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, long l) {
        return stack(level, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, long l) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, long l) {
        return stack(level, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, long l) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, long l) {
        return stack(LEVEL5, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, long l) {
        return stack(level, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, long l) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, long l) {
        return stack(level, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(Object[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, Object[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, Object[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, Object[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, Object[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, Object[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, Object[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, Object[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, Object[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(byte[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, byte[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, byte[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, byte[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, byte[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, byte[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, byte[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, byte[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, byte[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(char[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, char[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, char[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, char[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, char[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, char[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, char[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, char[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, char[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, char[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(double[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, double[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, double[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, double[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, double[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, double[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, double[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, double[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, double[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, double[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(float[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, float[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, float[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, float[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, float[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, float[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, float[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, float[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, float[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, float[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(int[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, int[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, int[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, int[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, int[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, int[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, int[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, int[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, int[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, int[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(long[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, long[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, long[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, long[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, long[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, long[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, long[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, long[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, long[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, long[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, DEFAULT_STACK_DEPTH);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(Object obj, int depth) {
        return stack(LEVEL5, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, Object obj, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, Object obj, int depth) {
        return stack(LEVEL5, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, Object obj, int depth) {
        return stack(level, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, Object obj, int depth) {
        return stack(level, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, Object obj, int depth) {
        return stack(level, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, Object obj, int depth) {
        return stack(LEVEL5, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, Object obj, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, Object obj, int depth) {
        return stack(LEVEL5, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, Object obj, int depth) {
        return stack(level, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, Object obj, int depth) {
        return stack(level, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, Object obj, int depth) {
        return stack(level, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(byte b, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, byte b, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, byte b, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, byte b, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, byte b, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, byte b, int depth) {
        return stack(level, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, byte b, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, byte b, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, byte b, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, byte b, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, byte b, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, byte b, int depth) {
        return stack(level, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(char c, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, char c, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, char c, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, char c, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, char c, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, char c, int depth) {
        return stack(level, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, char c, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, char c, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, char c, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, char c, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, char c, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, char c, int depth) {
        return stack(level, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(double d, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, double d, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, double d, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, double d, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, double d, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, double d, int depth) {
        return stack(level, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, double d, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, double d, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, double d, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, double d, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, double d, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, double d, int depth) {
        return stack(level, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(float f, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, float f, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, float f, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, float f, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, float f, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, float f, int depth) {
        return stack(level, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, float f, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, float f, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, float f, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, float f, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, float f, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, float f, int depth) {
        return stack(level, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(int i, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, int i, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, int i, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, int i, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, int i, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, int i, int depth) {
        return stack(level, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, int i, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, int i, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, int i, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, int i, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, int i, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, int i, int depth) {
        return stack(level, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(long l, int depth) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, long l, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, long l, int depth) {
        return stack(LEVEL5, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, long l, int depth) {
        return stack(level, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, long l, int depth) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, long l, int depth) {
        return stack(level, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, long l, int depth) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, long l, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, long l, int depth) {
        return stack(LEVEL5, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, long l, int depth) {
        return stack(level, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, long l, int depth) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, long l, int depth) {
        return stack(level, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(Object[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, Object[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, Object[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, Object[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, Object[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, Object[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, Object[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, Object[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, Object[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, Object[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, Object[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, Object[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(byte[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, byte[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, byte[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, byte[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, byte[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, byte[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, byte[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, byte[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, byte[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, byte[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, byte[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, byte[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(char[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, char[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, char[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, char[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, char[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, char[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, char[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, char[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, char[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, char[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, char[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, char[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(double[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, double[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, double[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, double[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, double[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, double[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, double[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, double[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, double[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, double[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, double[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, double[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(float[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, float[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, float[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, float[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, float[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, float[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, float[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, float[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, float[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, float[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, float[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, float[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(int[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, int[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, int[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, int[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, int[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, int[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, int[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, int[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, int[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, int[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, int[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, int[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(long[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, long[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, long[] ary, int depth) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, long[] ary, int depth) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, long[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, long[] ary, int depth) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(String name, long[] ary, int depth) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(ANSIColor color, String name, long[] ary, int depth) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(ANSIColor[] colors, String name, long[] ary, int depth) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the default foreground and background.
      */
    public static boolean stack(QlLevel level, String name, long[] ary, int depth) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified color.
      */
    public static boolean stack(QlLevel level, ANSIColor color, String name, long[] ary, int depth) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes stack output, with the specified colors.
      */
    public static boolean stack(QlLevel level, ANSIColor[] colors, String name, long[] ary, int depth) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, depth);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String msg) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { NONE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { NONE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { NONE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(byte b) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(char c) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(double d) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(float f) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(int i) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(long l) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { NONE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { NONE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { NONE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with none foreground on the default background.
      */
    public static boolean none(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { NONE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String msg) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { BOLD }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { BOLD }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { BOLD }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(byte b) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(char c) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(double d) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(float f) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(int i) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(long l) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { BOLD }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { BOLD }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with bold foreground on the default background.
      */
    public static boolean bold(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { BOLD }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String msg) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(byte b) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(char c) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(double d) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(float f) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(int i) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(long l) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underscore foreground on the default background.
      */
    public static boolean underscore(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { UNDERSCORE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String msg) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { UNDERLINE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(byte b) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(char c) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(double d) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(float f) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(int i) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(long l) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with underline foreground on the default background.
      */
    public static boolean underline(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { UNDERLINE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String msg) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { BLINK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { BLINK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { BLINK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(char c) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(double d) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(float f) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(int i) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(long l) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { BLINK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { BLINK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blink foreground on the default background.
      */
    public static boolean blink(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { BLINK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String msg) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { REVERSE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { REVERSE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { REVERSE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(byte b) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(char c) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(double d) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(float f) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(int i) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(long l) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { REVERSE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { REVERSE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with reverse foreground on the default background.
      */
    public static boolean reverse(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { REVERSE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String msg) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { CONCEALED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(byte b) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(char c) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(double d) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(float f) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(int i) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(long l) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with concealed foreground on the default background.
      */
    public static boolean concealed(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { CONCEALED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String msg) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { BLACK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { BLACK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { BLACK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(char c) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(double d) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(float f) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(int i) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(long l) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { BLACK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { BLACK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with black foreground on the default background.
      */
    public static boolean black(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String msg) {
        return stack(LEVEL5, new ANSIColor[] { RED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { RED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { RED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { RED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(byte b) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(char c) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(double d) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(float f) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(int i) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(long l) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { RED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { RED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with red foreground on the default background.
      */
    public static boolean red(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String msg) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { GREEN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { GREEN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { GREEN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(byte b) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(char c) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(double d) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(float f) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(int i) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(long l) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { GREEN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { GREEN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with green foreground on the default background.
      */
    public static boolean green(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String msg) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { YELLOW }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { YELLOW }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { YELLOW }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(byte b) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(char c) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(double d) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(float f) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(int i) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(long l) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { YELLOW }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { YELLOW }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with yellow foreground on the default background.
      */
    public static boolean yellow(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String msg) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { BLUE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { BLUE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { BLUE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(char c) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(double d) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(float f) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(int i) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(long l) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { BLUE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { BLUE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with blue foreground on the default background.
      */
    public static boolean blue(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String msg) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { MAGENTA }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(byte b) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(char c) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(double d) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(float f) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(int i) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(long l) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with magenta foreground on the default background.
      */
    public static boolean magenta(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String msg) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { CYAN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { CYAN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { CYAN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(byte b) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(char c) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(double d) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(float f) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(int i) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(long l) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { CYAN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { CYAN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with cyan foreground on the default background.
      */
    public static boolean cyan(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String msg) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { WHITE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { WHITE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { WHITE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(byte b) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(char c) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(double d) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(float f) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(int i) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(long l) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { WHITE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { WHITE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with white foreground on the default background.
      */
    public static boolean white(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_BLACK }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a black background.
      */
    public static boolean onBlack(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_BLACK }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_RED }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_RED }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_RED }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_RED }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_RED }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a red background.
      */
    public static boolean onRed(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_RED }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_GREEN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a green background.
      */
    public static boolean onGreen(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_GREEN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a yellow background.
      */
    public static boolean onYellow(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_YELLOW }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_BLUE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a blue background.
      */
    public static boolean onBlue(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_BLUE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a magenta background.
      */
    public static boolean onMagenta(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_MAGENTA }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_CYAN }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a cyan background.
      */
    public static boolean onCyan(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_CYAN }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String msg) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String msg) {
        return stack(level, new ANSIColor[] { ON_WHITE }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, Object obj) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, Object obj) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, byte b) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, byte b) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, char c) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, char c) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, double d) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, double d) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, float f) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, float f) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, int i) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, int i) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, long l) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, long l) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, char[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, char[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, double[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, double[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, float[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, float[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, int[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, int[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, long[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground on a white background.
      */
    public static boolean onWhite(QlLevel level, String name, long[] ary) {
        return stack(level, new ANSIColor[] { ON_WHITE }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String msg) {
        return stack(LEVEL5, NO_COLORS, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String msg) {
        return stack(LEVEL5, new ANSIColor[] { color }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String msg) {
        return stack(LEVEL5, colors, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String msg) {
        return stack(level, NO_COLORS, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String msg) {
        return stack(level, new ANSIColor[] { color }, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String msg) {
        return stack(level, colors, msg, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(Object obj) {
        return stack(LEVEL5, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, Object obj) {
        return stack(LEVEL5, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, Object obj) {
        return stack(level, NO_COLORS, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, Object obj) {
        return stack(level, new ANSIColor[] { color }, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, Object obj) {
        return stack(level, colors, null, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, Object obj) {
        return stack(LEVEL5, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, Object obj) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, Object obj) {
        return stack(LEVEL5, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, Object obj) {
        return stack(level, NO_COLORS, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, Object obj) {
        return stack(level, new ANSIColor[] { color }, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, Object obj) {
        return stack(level, colors, name, obj, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(byte b) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, byte b) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, byte b) {
        return stack(LEVEL5, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, byte b) {
        return stack(level, NO_COLORS, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, byte b) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, byte b) {
        return stack(level, colors, null, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, byte b) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, byte b) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, byte b) {
        return stack(LEVEL5, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, byte b) {
        return stack(level, NO_COLORS, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, byte b) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, byte b) {
        return stack(level, colors, name, String.valueOf(b), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(char c) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, char c) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, char c) {
        return stack(LEVEL5, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, char c) {
        return stack(level, NO_COLORS, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, char c) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, char c) {
        return stack(level, colors, null, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, char c) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, char c) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, char c) {
        return stack(LEVEL5, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, char c) {
        return stack(level, NO_COLORS, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, char c) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, char c) {
        return stack(level, colors, name, String.valueOf(c), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(double d) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, double d) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, double d) {
        return stack(LEVEL5, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, double d) {
        return stack(level, NO_COLORS, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, double d) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, double d) {
        return stack(level, colors, null, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, double d) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, double d) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, double d) {
        return stack(LEVEL5, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, double d) {
        return stack(level, NO_COLORS, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, double d) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, double d) {
        return stack(level, colors, name, String.valueOf(d), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(float f) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, float f) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, float f) {
        return stack(LEVEL5, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, float f) {
        return stack(level, NO_COLORS, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, float f) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, float f) {
        return stack(level, colors, null, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, float f) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, float f) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, float f) {
        return stack(LEVEL5, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, float f) {
        return stack(level, NO_COLORS, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, float f) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, float f) {
        return stack(level, colors, name, String.valueOf(f), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(int i) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, int i) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, int i) {
        return stack(LEVEL5, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, int i) {
        return stack(level, NO_COLORS, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, int i) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, int i) {
        return stack(level, colors, null, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, int i) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, int i) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, int i) {
        return stack(LEVEL5, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, int i) {
        return stack(level, NO_COLORS, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, int i) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, int i) {
        return stack(level, colors, name, String.valueOf(i), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(long l) {
        return stack(LEVEL5, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, long l) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, long l) {
        return stack(LEVEL5, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, long l) {
        return stack(level, NO_COLORS, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, long l) {
        return stack(level, new ANSIColor[] { color }, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, long l) {
        return stack(level, colors, null, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, long l) {
        return stack(LEVEL5, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, long l) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, long l) {
        return stack(LEVEL5, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, long l) {
        return stack(level, NO_COLORS, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, long l) {
        return stack(level, new ANSIColor[] { color }, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, long l) {
        return stack(level, colors, name, String.valueOf(l), NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(Object[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, Object[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, Object[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, Object[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, Object[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, Object[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, Object[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, Object[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, Object[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, Object[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, Object[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(byte[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, byte[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, byte[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, byte[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, byte[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, byte[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, byte[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, byte[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, byte[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, byte[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, byte[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(char[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, char[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, char[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, char[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, char[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, char[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, char[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, char[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, char[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, char[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, char[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(double[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, double[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, double[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, double[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, double[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, double[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, double[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, double[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, double[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, double[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, double[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(float[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, float[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, float[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, float[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, float[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, float[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, float[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, float[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, float[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, float[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, float[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(int[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, int[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, int[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, int[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, int[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, int[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, int[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, int[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, int[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, int[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, int[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(long[] ary) {
        return stack(LEVEL5, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, long[] ary) {
        return stack(LEVEL5, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, long[] ary) {
        return stack(level, NO_COLORS, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, long[] ary) {
        return stack(level, new ANSIColor[] { color }, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, long[] ary) {
        return stack(level, colors, null, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(String name, long[] ary) {
        return stack(LEVEL5, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(ANSIColor color, String name, long[] ary) {
        return stack(LEVEL5, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(ANSIColor[] colors, String name, long[] ary) {
        return stack(LEVEL5, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the default foreground and background.
      */
    public static boolean log(QlLevel level, String name, long[] ary) {
        return stack(level, NO_COLORS, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified color.
      */
    public static boolean log(QlLevel level, ANSIColor color, String name, long[] ary) {
        return stack(level, new ANSIColor[] { color }, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }

    /** 
      * Writes logging output, with the specified colors.
      */
    public static boolean log(QlLevel level, ANSIColor[] colors, String name, long[] ary) {
        return stack(level, colors, name, ary, NO_COLOR, NO_COLOR, NO_COLOR, 1);
    }


    //--- end of autogenerated section.


    
    protected static StackTraceElement[] getStack(int depth) {
        return (new Exception("")).getStackTrace();
    }    

}
