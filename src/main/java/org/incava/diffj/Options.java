package org.incava.diffj;

import java.util.Arrays;
import java.util.List;
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
    public static final String VERSION = "1.2.1";

    public static final String DEFAULT_SOURCE = Java.SOURCE_1_5;

    /**
     * Whether to use brief or detailed reporting.
     */
    public boolean briefOutput = false;

    /**
     * Whether to show context (detailed reporting only).
     */
    public boolean contextOutput = false;

    /**
     * Whether to use colors for highlighting
     */
    public boolean highlightOutput = false;

    /**
     * The Java source version, of the from-file.
     */
    private String fromSource = DEFAULT_SOURCE;

    /**
     * The Java source version, of the to-file.
     */
    private String toSource = DEFAULT_SOURCE;

    /**
     * Whether to recurse.
     */
    private Boolean recurse = false;

    /**
     * The name of the first file, if not the actual name.
     */
    private String firstFileName;

    /**
     * The name of the second file, if not the actual name.
     */
    private String secondFileName;

    private BooleanOption briefOpt;
    private BooleanOption contextOpt;
    private IntegerOption tabWidthOpt;
    private BooleanOption verboseOpt;
    private BooleanOption versionOpt;
    private StringOption fromSourceOpt;
    private StringOption toSourceOpt;
    private StringOption sourceOpt;
    private BooleanOption recurseOpt;
    private BooleanOption highlightOpt;

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

        briefOpt      = addBooleanOption("brief",     "Display output in brief form");
        contextOpt    = addBooleanOption("context",   "Show context (non-brief form only)");
        highlightOpt  = addBooleanOption("highlight", "Whether to use colors (context output only)");
        tabWidthOpt   = addOption(new IntegerOption("tabwidth",  "The number of spaces to treat tabs equal to"));
        recurseOpt    = addBooleanOption("recurse",   "Process directories recursively");
        verboseOpt    = addBooleanOption("verbose",   "Whether to run in verbose mode (for debugging)");
        versionOpt    = addBooleanOption("version",   "Displays the version");
        recurseOpt.setShortName('r');
        versionOpt.setShortName('v');

        String javaVersions = StringExt.join(Arrays.asList(new String[] {
                    Java.SOURCE_1_3,
                    Java.SOURCE_1_4,
                    Java.SOURCE_1_5 + "(the default)",
                    "or " + Java.SOURCE_1_6,
                }), ",");
        
        fromSourceOpt = addOption(new StringOption("from-source", "The Java source version, of the from-file; " + javaVersions));
        toSourceOpt   = addOption(new StringOption("to-source",   "The Java source version, of the to-file; " + javaVersions));
        sourceOpt     = addOption(new StringOption("source",      "The Java source version of both from-file and the to-file"));

        BooleanOption unifiedOption = new BooleanOption("unified",   "Output unified context. Unused; for compatibility with GNU diff");
        addOption(unifiedOption);
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
        addOption(nameOpt);

        BooleanOption fmtOpt = new BooleanOption("format",   "Sets the format to unified diff; ignored");
        fmtOpt.setShortName('u');
        addOption(fmtOpt);
        
        // addEnvironmentVariable("DIFFJ_PROPERTIES");
        
        addRunControlFile("/etc/diffj.conf");
        addRunControlFile("~/.diffjrc");
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public List<String> process(List<String> args) {
        List<String> unprocessed = super.process(args);

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


    /**
     * Whether to use brief or detailed reporting.
     */
    public boolean showBriefOutput() {
        return briefOutput;
    }

    /**
     * Whether to show context (detailed reporting only).
     */
    public boolean showContextOutput() {
        return contextOutput;
    }

    /**
     * Whether to use colors for highlighting
     */
    public boolean highlightOutput() {
        return highlightOutput;
    }

    /**
     * The Java source version, of the from-file.
     */
    public String getFromSource() {
        return fromSource;
    }

    /**
     * The Java source version, of the to-file.
     */
    public String getToSource() {
        return toSource;
    }

    /**
     * Whether to recurse.
     */
    public Boolean recurse() {
        return recurse;
    }

    /**
     * The name of the first file, if not the actual name.
     */
    public String getFirstFileName() {
        return firstFileName;
    }

    /**
     * The name of the second file, if not the actual name.
     */
    public String getSecondFileName() {
        return secondFileName;
    }
}
