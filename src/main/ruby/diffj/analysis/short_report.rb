#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/report'

include Java

java_import org.incava.analysis.Report

module DiffJ
  module Analysis
    # Reports differences in short form.
    class ShortReport < Rpt
      include Loggable
      
      def initialize writer
        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_green
        super
      end

      def flush
        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_green
        report_flush
      end

      # Returns the given difference, in brief format.
      def to_string fdiff
        sb = java.lang.StringBuilder.new
        sb.append(fdiff.toDiffSummaryString())
        sb.append(": ")
        sb.append(fdiff.getMessage())
        sb.append("\n");
        return sb.toString()
      end
      
      def write_differences
        begin
          diffs = get_differences
          laststr = nil
          diffs.each do |fdiff|
            str = to_string fdiff
            if str != laststr
              writer.write str
              laststr = str
            end
          end
          # we can't close STDOUT
          writer.flush
        rescue java.io.IOException => ioe
        end
      end
    end
  end
end
