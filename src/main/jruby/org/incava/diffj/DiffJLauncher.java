package org.incava.diffj;

import java.io.StringWriter;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ScriptingContainer;

public class DiffJLauncher {
    public static void main(String[] args) {
        StringWriter errorWriter = null;

        try {
            errorWriter = new StringWriter();
            ScriptingContainer container = new ScriptingContainer();
            container.setError(errorWriter);
            // I think this requires 1.6.0+:
            container.setArgv(args);

            StringBuilder sb = new StringBuilder();
            sb.append("require 'diffj/app/cli'\n");
            sb.append("DiffJ::CLI.run\n");
            
            Object obj = container.runScriptlet(sb.toString());
            System.err.println("returned object: " + obj);
            System.err.println("returned object: " + obj.getClass());

            Long exitValue = (Long)obj;
            System.err.println("exitValue: " + exitValue);
        }
        catch (EvalFailedException e) {
            System.out.println("e: " + e.getMessage());
        } 
        finally {
            if (errorWriter != null) {
                System.out.println("ew: " + errorWriter.toString());
            }
        }
    }
}
