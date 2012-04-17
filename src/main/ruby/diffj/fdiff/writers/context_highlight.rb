#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/writers/context'

include Java

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent

      class ContextHighlightWriter < ContextWriter
        include Loggable

        # The color for added code.
        COLOR_ADDED = org.incava.ijdk.util.ANSI::YELLOW

        # The color for deleted code.
        COLOR_DELETED = org.incava.ijdk.util.ANSI::RED

        COLOR_RESET = org.incava.ijdk.util.ANSI::RESET

        def get_line lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete
          line = lines[lidx - 1]

          sb = java.lang.StringBuilder.new

          # PMD reports columns using tabSize == 8, so we replace tabs with
          # spaces here.
          # ... I loathe tabs.
          
          line = line.gsub("\t", "        ");
        
          llen = line.length()
        
          # columns are 1-indexed, strings are 0-indexed
          # ... half my life is adding or substracting one.
         
          fcol = fromLine == lidx ? fromColumn - 1 : 0;
          tcol = toLine   == lidx ? toColumn       : llen;
        
          sb.append("! ").append(line[0 ... fcol])
          
          # highlight:
          
          String highlightColor = isDelete ? COLOR_DELETED : COLOR_ADDED;
          
          sb.append(highlightColor);
          sb.append(line[fcol ... tcol])
          sb.append(COLOR_RESET);
          
          sb.append(line[tcol ... llen])
          sb.append(EOLN)

          sb.toString()
        end
      end
    end
  end
end
