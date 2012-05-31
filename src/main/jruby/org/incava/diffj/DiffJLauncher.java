package org.incava.diffj;

import java.io.StringWriter;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;

public class DiffJLauncher {
    private static long start = System.currentTimeMillis();

    public static void log(String msg) {
        long currTime = System.currentTimeMillis();
        // System.out.printf("%10d %4d %s\n", currTime, currTime - start, msg);
    }

    public static void main(String[] args) {
        StringWriter errorWriter = null;

        long start = System.currentTimeMillis();
        log("starting ... ");

        try {
            errorWriter = new StringWriter();
            log("loading container ... ");

            ScriptingContainer container = new ScriptingContainer();
            container.setError(errorWriter);
            // I think this requires 1.6.0+:
            container.setArgv(args);

            log("creating script ... ");

            StringBuilder sb = new StringBuilder();
            sb.append("require 'diffj/app/cli'\n");
            sb.append("DiffJ::CLI.run\n");
            // sb.append("1\n");

            log("running script ... ");
            
            Object obj = container.runScriptlet(sb.toString());

            log("exiting ... ");

            Long exitValue = (Long)obj;
            System.exit(exitValue.intValue());
        }
        catch (EvalFailedException e) {
            System.out.println("e: " + e.getMessage());
            System.exit(-1);
        } 
    }
}
