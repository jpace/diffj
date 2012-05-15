#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'diffj/fdiff/writers/writer'
require 'riel/log'

include Java

module DiffJ
  module FDiff
    module Writer
      class NoContextWriter < BaseWriter
        include Loggable
        
        def initialize from_contents, to_contents
          @from_contents = from_contents
          @to_contents = to_contents
        end
        
        def print_from str, fdiff
          print_lines_by_location str, fdiff.first_location, "<", @from_contents
        end
        
        def print_to str, fdiff
          print_lines_by_location str, fdiff.second_location, ">", @to_contents
        end
        
        def print_lines str, fdiff
          fdiff.print_no_context self, str
          str << EOLN
        end

        def print_lines_by_location str, locrg, ind, lines
          from_line = locrg.from.line
          through_line = locrg.to.line
          (from_line .. through_line).each do |lnum|
            str << ind + " " + lines[lnum - 1]
            str << EOLN
          end
        end
      end
    end
  end
end
