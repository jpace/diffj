#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/io/diffwriter/writer'

include Java

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent

      class ContextWriter < DiffJ::IO::Diff::Writer
        include Loggable

        def initialize from_contents, to_contents
          # super
          
          @from_contents = from_contents
          @to_contents = to_contents

          info "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$".green
        end

        def print_from sb, fdiff
          print_lines_by_location sb, true, fdiff, fdiff.first_location, @from_contents
        end

        def print_to sb, fdiff
          print_lines_by_location sb, false, fdiff, fdiff.second_location, @to_contents
        end

        def print_lines sb, fdiff
          info "fdiff: #{fdiff}; #{fdiff.class}".yellow
          fdiff.print_context self, sb
          sb.append EOLN
        end

        def print_lines sb, fdiff
          info "fdiff: #{fdiff}; #{fdiff.class}".yellow
          fdiff.print_context self, sb
          sb.append EOLN
        end

        def get_line lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete
          sb = java.lang.StringBuilder.new
          sb.append("! ").append(lines[lidx - 1]).append(EOLN);
          return sb.toString()
        end

        def print_lines_by_location sb, isDelete, fdiff, locrg, lines
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
            line = get_line(lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete)
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
