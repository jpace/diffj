package org.incava.diffj;

import java.io.StringWriter;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;

public class DiffJLauncher {
    public static void main(String[] args) {
        StringWriter errorWriter = null;

        long start = System.currentTimeMillis();
        System.out.println("starting ... " + (System.currentTimeMillis() - start));

        try {
            errorWriter = new StringWriter();
            System.out.println("loading container ... " + (System.currentTimeMillis() - start));

            ScriptingContainer container = new ScriptingContainer();
            container.setError(errorWriter);
            // I think this requires 1.6.0+:
            container.setArgv(args);

            System.out.println("creating script ... " + (System.currentTimeMillis() - start));

            StringBuilder sb = new StringBuilder();
            sb.append("require 'diffj/app/cli'\n");
            sb.append("DiffJ::CLI.run\n");

            System.out.println("running script ... " + (System.currentTimeMillis() - start));
            
            Object obj = container.runScriptlet(sb.toString());

            System.out.println("exiting ... " + (System.currentTimeMillis() - start));

            Long exitValue = (Long)obj;
            System.exit(exitValue.intValue());
        }
        catch (EvalFailedException e) {
            System.out.println("e: " + e.getMessage());
            System.exit(-1);
        } 
    }
}
