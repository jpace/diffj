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

            StringBuilder sb = new StringBuilder();
            sb.append("puts 'hello, world'\n");
            sb.append("$CMD_ARGS = Array.new\n");
            for (String arg : args) {
                sb.append("$CMD_ARGS << '" + arg + "'\n");
            }
            sb.append("require 'diffj/app/cli'");
            System.out.println("cmd: " + sb);        
            container.runScriptlet(sb.toString());
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
