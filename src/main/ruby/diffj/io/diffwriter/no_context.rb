#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

java_import org.incava.analysis.DiffNoContextWriter
java_import org.incava.analysis.DiffContextHighlightWriter

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent
      
      class NoContextWriter < DiffNoContextWriter
        include Loggable
        
        def initialize from_contents, to_contents
          super
          @from_contents = from_contents
          @to_contents = to_contents
        end
        
        def noctx_print_from sb, fdiff
          info "self: #{self}".on_red
          noctx_print_lines_by_location sb, fdiff.getFirstLocation(), "<", @from_contents
        end
        
        def noctx_print_to sb, fdiff
          info "self: #{self}".on_red
          noctx_print_lines_by_location sb, fdiff.getSecondLocation(), ">", @to_contents
        end
        
        def noctx_print_lines sb, fdiff
          fdiff.printNoContext self, sb
          sb.append EOLN
        end

        def noctx_print_lines_by_location sb, locrg, ind, lines
          fromLine = locrg.getStart().getLine()
          throughLine = locrg.getEnd().getLine()
          (fromLine .. throughLine).each do |lnum|
            info "lnum: #{lnum}".green
            sb.append ind + " " + lines[lnum - 1]
            info "sb: #{sb}".green
            sb.append EOLN
          end
        end
      end
    end
  end
end
