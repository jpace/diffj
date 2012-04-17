#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/writers/writer'

include Java

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent
      
      class NoContextWriter < DiffJ::IO::Diff::Writer
        include Loggable
        
        def initialize from_contents, to_contents
          @from_contents = from_contents
          @to_contents = to_contents
        end
        
        def print_from sb, fdiff
          print_lines_by_location sb, fdiff.first_location, "<", @from_contents
        end
        
        def print_to sb, fdiff
          print_lines_by_location sb, fdiff.second_location, ">", @to_contents
        end
        
        def print_lines sb, fdiff
          fdiff.print_no_context self, sb
          sb.append EOLN
        end

        def print_lines_by_location sb, locrg, ind, lines
          fromLine = locrg.getStart().getLine()
          throughLine = locrg.getEnd().getLine()
          (fromLine .. throughLine).each do |lnum|
            sb.append ind + " " + lines[lnum - 1]
            sb.append EOLN
          end
        end
      end
    end
  end
end
