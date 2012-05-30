#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/fdiff/reports/report'

# Reports differences in short form. 
module DiffJ::FDiff::Report
  class ShortReport < BaseReport
    include Loggable
    
    # Returns the given difference, in brief format.
    def to_string fdiff
      str  = ""
      str << fdiff.to_diff_summary_string
      str << ": "
      str << fdiff.message
      str << "\n"
      return str
    end
    
    def write_differences
      laststr = nil
      differences.each do |fdiff|
        str = to_string fdiff
        if str != laststr
          @writer.write str
          laststr = str
        end
      end
      # we can't close STDOUT
      @writer.flush
    end
  end
end
