#!/usr/bin/jruby -w
# -*- ruby -*-

module DiffJ
  module FDiff
    module Writer
      EOLN = "\n"               # $$$ @todo make OS-independent

      class BaseWriter
        # Returns a string representing the given reference, consistent with the
        # format of the Report subclass.
        def difference fdiff
          str = ""
          print_diff_summary str, fdiff
          print_lines str, fdiff
          str
        end

        def print_diff_summary str, fdiff
          str << fdiff.to_diff_summary_string
          str << ' '
          str << fdiff.message
          str << EOLN
        end
      end
    end
  end
end
