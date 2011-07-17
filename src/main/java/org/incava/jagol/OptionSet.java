package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * A group of options.
 */
public class OptionSet {
    private final List<Option> options;

    private final List<String> rcFileNames;

    private final String appName;
    
    private final String description;
    
    public OptionSet(String appName, String description) {
        this.appName = appName;
        this.description = description;
        this.options = new ArrayList<Option>();
        this.rcFileNames = new ArrayList<String>();
    }
    
    /**
     * Returns the application name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds an options to this set.
     */
    public void add(Option opt) {
        options.add(opt);
    }

    /**
     * Adds a run control file to be processed.
     */
    public void addRunControlFile(String name) {
        tr.Ace.log("adding rc file: " + name);
        rcFileNames.add(name);
    }

    /**
     * Processes the run control files and command line arguments. Returns the
     * arguments that were not consumed by option processing.
     */
    public String[] process(String[] args) {
        tr.Ace.log("args: " + args);

        processRunControlFiles();

        return processCommandLine(args);
    }

    /**
     * Processes the run control files, if any.
     */
    protected void processRunControlFiles() {
        tr.Ace.log("");
        for (String rcFileName : rcFileNames) {
            tr.Ace.log("processing: " + rcFileName);
            try {
                Properties props = new Properties();

                int tildePos = rcFileName.indexOf('~');
                if (tildePos != -1) {
                    rcFileName = rcFileName.substring(0, tildePos) + System.getProperty("user.home") + rcFileName.substring(tildePos + 1);
                }

                props.load(new FileInputStream(rcFileName));

                tr.Ace.log("properties: " + props);
                Iterator pit = props.keySet().iterator();
                while (pit.hasNext()) {
                    String key   = (String)pit.next();
                    String value = (String)props.get(key);
                    tr.Ace.log(key + " => " + value);
                    
                    Iterator<Option> oit = options.iterator();
                    boolean processed = false;
                    while (!processed && oit.hasNext()) {
                        Option opt = oit.next();
                        tr.Ace.log("option: " + opt.getLongName());
                        if (opt.getLongName().equals(key)) {
                            tr.Ace.log("option matches: " + opt);
                            processed = true;
                            try {
                                opt.setValue(value);
                            }
                            catch (OptionException oe) {
                                tr.Ace.log("option exception: " + oe);
                                System.err.println("error: " + oe.getMessage());
                            }
                        }
                    }
                }
            }
            catch (IOException ioe) {
                tr.Ace.log("exception: " + ioe);
                // ioe.printStackTrace();
            }
        }
    }

    /**
     * Processes the command line arguments. Returns the arguments that were not
     * consumed by option processing.
     */
    protected String[] processCommandLine(String[] args) {
        tr.Ace.log("args: " + args);
        
        List<String> argList = new ArrayList<String>(Arrays.asList(args));

        tr.Ace.log("arg list: " + argList);

        while (!argList.isEmpty()) {
            String arg = argList.get(0);
            
            tr.Ace.log("arg: " + arg);
            
            if (arg.equals("--")) {
                argList.remove(0);
                break;
            }
            else if (arg.charAt(0) == '-') {
                tr.Ace.log("got leading dash");
                argList.remove(0);
                
                Iterator<Option> oit = options.iterator();
                boolean processed = false;
                while (!processed && oit.hasNext()) {
                    Option opt = oit.next();
                    tr.Ace.log("option: " + opt);
                    try {
                        processed = opt.set(arg, argList);
                        tr.Ace.log("processed: " + processed);
                    }
                    catch (OptionException oe) {
                        tr.Ace.log("option exception: " + oe);
                        System.err.println("error: " + oe.getMessage());
                    }
                }

                if (!processed) {
                    tr.Ace.log("argument not processed: '" + arg + "'");
                    if (arg.equals("--help") || arg.equals("-h")) {
                        showUsage();
                    }
                    else if (!rcFileNames.isEmpty() && arg.equals("--help - config")) {
                        showConfig();
                    }
                    else {
                        System.err.println("invalid option: " + arg + " (-h will show valid options)");
                    }
                    
                    break;
                }
            }
            else {
                break;
            }
        }

        String[] unprocessed = argList.toArray(new String[0]);
        
        tr.Ace.log("args", args);
        tr.Ace.log("unprocessed", unprocessed);

        return unprocessed;
    }

    protected void showUsage() {
        tr.Ace.log("generating help");

        System.out.println("Usage: " + appName + " [options] file...");
        System.out.println(description);
        System.out.println();
        System.out.println("Options:");

        tr.Ace.log("options: " + options);

        List<String> tags = new ArrayList<String>();

        for (Option opt : options) {
            tr.Ace.log("opt: " + opt);
            StringBuffer buf = new StringBuffer("  ");

            if (opt.getShortName() == 0) {
                buf.append("    ");
            }
            else {
                buf.append("-" + opt.getShortName() + ", ");
            }
                            
            buf.append("--" + opt.getLongName());
                            
            tags.add(buf.toString());
        }
                        
        int widest = -1;
        for (String tag : tags) {
            widest = Math.max(tag.length(), widest);
        }

        for (int idx = 0; idx < options.size(); ++idx) {
            Option opt = options.get(idx);
            String tag = tags.get(idx);
            tr.Ace.log("opt: " + opt);

            System.out.print(tag);
            for (int ti = tag.length(); ti < widest + 2; ++ti) {
                System.out.print(" ");
            }

            System.out.println(opt.getDescription());
        }

        if (!rcFileNames.isEmpty()) {
            System.out.println("For an example configure file, run --help - config");
            System.out.println();
            System.out.println("Configuration File" + (rcFileNames.size() > 1 ? "s" : "") + ":");
            for (String rcFileName : rcFileNames) {
                System.out.println("    " + rcFileName);
            }
        }
    }

    protected void showConfig() {
        tr.Ace.log("generating config");

        for (Option opt : options) {
            System.out.println("# " + opt.getDescription());
            System.out.println(opt.getLongName() + " = " + opt.toString());
            System.out.println();
        }
    }
}
