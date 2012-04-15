#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/report'

include Java

module DiffJ
  module Analysis
    # Reports differences in short form.
    class ShortReport < Report
      include Loggable
      
      # Returns the given difference, in brief format.
      def to_string fdiff
        str  = ""
        str << fdiff.toDiffSummaryString()
        str << ": "
        str << fdiff.message
        str << "\n"
        return str
      end
      
      def write_differences
        begin
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
        rescue java.io.IOException => ioe
        end
      end
    end
  end
end
