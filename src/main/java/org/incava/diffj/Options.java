package org.incava.diffj;

import java.util.Arrays;
import org.incava.analysis.DetailedReport;
import org.incava.ijdk.lang.StringExt;
import org.incava.jagol.BooleanOption;
import org.incava.jagol.IntegerOption;
import org.incava.jagol.OptionSet;
import org.incava.jagol.StringOption;
import org.incava.java.Java;

/**
 * Options.
 */
public class Options extends OptionSet {
    public static int MAXIMUM_WARNING_LEVEL = 100;
    public static int MINIMUM_WARNING_LEVEL = -1;
    public static int DEFAULT_WARNING_LEVEL = -1;

    /**
     * Whether to use brief or detailed reporting.
     */
    public static boolean briefOutput = false;

    /**
     * Whether to show context (detailed reporting only).
     */
    public static boolean contextOutput = false;

    /**
     * Whether to use colors for highlighting
     */
    public static boolean highlightOutput = false;

    public static String VERSION = "1.2.1";

    public static String DEFAULT_SOURCE = Java.SOURCE_1_5;

    /**
     * The Java source version, of the from-file.
     */
    public static String fromSource = DEFAULT_SOURCE;

    /**
     * The Java source version, of the to-file.
     */
    public static String toSource = DEFAULT_SOURCE;

    /**
     * Whether to recurse.
     */
    public static Boolean recurse = Boolean.FALSE;

    /**
     * The brief option.
     */
    private BooleanOption briefOpt;

    /**
     * The context option.
     */
    private BooleanOption contextOpt;

    /**
     * The tab width option.
     */
    private IntegerOption tabWidthOpt;

    /**
     * The verbose option.
     */
    private BooleanOption verboseOpt;

    /**
     * The version option.
     */
    private BooleanOption versionOpt;

    /**
     * The from-source option.
     */
    private StringOption fromSourceOpt;

    /**
     * The to-source option.
     */
    private StringOption toSourceOpt;

    /**
     * The source option.
     */
    private StringOption sourceOpt;

    /**
     * Whether to process directories recursively.
     */
    private BooleanOption recurseOpt;

    /**
     * Whether to highlight.
     */
    private BooleanOption highlightOpt;

    /**
     * The name of the first file, if not the actual name.
     */
    public String firstFileName;

    /**
     * The name of the second file, if not the actual name.
     */
    public String secondFileName;

    private static Options instance = new Options();

    public static Options get() {
        return instance;
    }

    protected Options() {
        super("diffj", "Analyzes and validates Java and Javadoc");

        // use the diffj.* equivalent property for each option.

        Boolean brief = new Boolean(briefOutput);
        String briefProperty = System.getProperty("diffj.brief");

        if (briefProperty != null) {
            brief = Boolean.valueOf(briefProperty);
            briefOutput = brief;
        }

        Integer tabWidth = new Integer(DetailedReport.tabWidth);
        String tabWidthProperty = System.getProperty("diffj.tabwidth");
        if (tabWidthProperty != null) {
            tabWidth = Integer.valueOf(tabWidthProperty);
            DetailedReport.tabWidth = tabWidth;
        }

        Boolean verbose = Boolean.FALSE;
        String verboseProperty = System.getProperty("diffj.verbose");
        if (verboseProperty != null) {
            verbose = new Boolean(verboseProperty);
        }

        add(briefOpt      = new BooleanOption("brief",     "Display output in brief form"));
        add(contextOpt    = new BooleanOption("context",   "Show context (non-brief form only)"));
        add(highlightOpt  = new BooleanOption("highlight", "Whether to use colors (context output only)"));
        add(tabWidthOpt   = new IntegerOption("tabwidth",  "The number of spaces to treat tabs equal to"));
        add(recurseOpt    = new BooleanOption("recurse",   "Process directories recursively"));
        add(verboseOpt    = new BooleanOption("verbose",   "Whether to run in verbose mode (for debugging)"));
        add(versionOpt    = new BooleanOption("version",   "Displays the version"));
        recurseOpt.setShortName('r');
        versionOpt.setShortName('v');

        String javaVersions = StringExt.join(Arrays.asList(new String[] {
                    Java.SOURCE_1_3,
                    Java.SOURCE_1_4,
                    Java.SOURCE_1_5 + "(the default)",
                    "or " + Java.SOURCE_1_6,
                }), ",");
        
        add(fromSourceOpt = new StringOption("from-source", "The Java source version, of the from-file; " + javaVersions));
        add(toSourceOpt   = new StringOption("to-source",   "The Java source version, of the to-file; " + javaVersions));
        add(sourceOpt     = new StringOption("source",      "The Java source version of both from-file and the to-file"));

        BooleanOption unifiedOption = new BooleanOption("unified",   "Output unified context. Unused; for compatibility with GNU diff");
        add(unifiedOption);
        unifiedOption.setShortName('u');

        // svn diff --diff-cmd cmd passes "-u, -L first, -L second, file1, file2":
        StringOption nameOpt = new StringOption("name",   "Sets the first/second name to be displayed") {
                public void setValue(String val) {
                    if (firstFileName == null) {
                        firstFileName = val;
                    }
                    else {
                        secondFileName = val;
                    }
                }
            };
        nameOpt.setShortName('l');
        nameOpt.setShortName('L');
        add(nameOpt);

        BooleanOption fmtOpt = new BooleanOption("format",   "Sets the format to unified diff; ignored");
        fmtOpt.setShortName('u');
        add(fmtOpt);
        
        // addEnvironmentVariable("DIFFJ_PROPERTIES");
        
        addRunControlFile("/etc/diffj.conf");
        addRunControlFile("~/.diffjrc");
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public String[] process(String[] args) {
        String[] unprocessed = super.process(args);

        Integer tabWidthInt = tabWidthOpt.getValue();
        if (tabWidthInt != null) {
            DetailedReport.tabWidth = tabWidthInt;
        }
    
        Boolean briefBool = briefOpt.getValue();
        if (briefBool != null) {
            briefOutput = briefBool;
        }
        
        Boolean contextBool = contextOpt.getValue();
        if (contextBool != null) {
            contextOutput = contextBool;

            // override to show detailed output
            if (contextOutput) {
                briefOutput = false;
                highlightOutput = true;
            }
        }
        
        Boolean highlightBool = highlightOpt.getValue();
        if (highlightBool != null) {
            highlightOutput = highlightBool;

            // override to show detailed output
            if (highlightOutput) {
                briefOutput = false;
            }
        }
        
        Boolean recurseBool = recurseOpt.getValue();
        if (recurseBool != null) {
            recurse = recurseBool;
        }

        Boolean verboseBool = verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.setVerbose(verboseBool);
        }

        Boolean versionBool = versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("diffj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        String sourceStr = sourceOpt.getValue();
        if (sourceStr != null) {
            fromSource = sourceStr;
            toSource = sourceStr;
        }

        // override from and to source if specifically set.

        String fromSourceStr = fromSourceOpt.getValue();
        if (fromSourceStr != null) {
            fromSource = fromSourceStr;
        }

        String toSourceStr = toSourceOpt.getValue();
        if (toSourceStr != null) {
            toSource = toSourceStr;
        }

        return unprocessed;
    }
}
