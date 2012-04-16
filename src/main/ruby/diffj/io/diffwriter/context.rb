#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

java_import org.incava.analysis.DiffContextWriter

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent

      class ContextWriter < DiffContextWriter
        include Loggable

        def initialize from_contents, to_contents
          super
          
          @from_contents = from_contents
          @to_contents = to_contents

          info "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$".green
        end

        def ctx_print_from sb, fdiff
          ctx_print_lines_by_location sb, true, fdiff, fdiff.getFirstLocation(), @from_contents
        end

        def ctx_print_to sb, fdiff
          ctx_print_lines_by_location sb, false, fdiff, fdiff.getSecondLocation(), @to_contents
        end

        def ctx_print_lines sb, fdiff
          fdiff.printContext self, sb
          sb.append EOLN
        end

        def ctx_get_line lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete
          sb = java.lang.StringBuilder.new
          sb.append("! ").append(lines[lidx - 1]).append(EOLN);
          return sb.toString()
        end

        def ctx_print_lines_by_location sb, isDelete, fdiff, locrg, lines
          fromLine = locrg.getStart().getLine();
          fromColumn = locrg.getStart().getColumn();
          toLine = locrg.getEnd().getLine();
          toColumn = locrg.getEnd().getColumn();

          ([ 0, fromLine - 4 ].max ... fromLine - 1).each do |lnum|
            sb.append("  ").append(lines[lnum])
            sb.append(EOLN);
          end

          # PMD reports columns using tabSize == 8, so we replace tabs with
          # spaces here.
          # ... I loathe tabs.
          (fromLine .. toLine).each do |lidx|
            line = ctx_get_line(lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete)
            sb.append(line);
          end

          (toLine ... [ toLine + 3, lines.size() ].min).each do |lnum|
            sb.append("  ").append(lines[lnum])
            sb.append(EOLN)
          end
        end
      end
    end
  end
end
