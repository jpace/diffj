#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    module Diff
      EOLN = "\n"               # $$$ @todo make OS-independent

      class Writer
        # Returns a string representing the given reference, consistent with the
        # format of the Report subclass.
        def difference fdiff
          sb = java.lang.StringBuilder.new

          print_diff_summary sb, fdiff
          print_lines sb, fdiff
          
          sb.toString();
        end

        def print_diff_summary sb, fdiff
          sb.append fdiff.to_diff_summary_string
          sb.append ' '
          sb.append fdiff.message
          sb.append EOLN
        end
      end
    end
  end
end
