#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

java_import org.incava.analysis.Report

module DiffJ
  module Analysis
    # Reports differences in long form.
    class Rpt < org.incava.analysis.Report
      include Loggable
      
      def initialize writer
        super writer
        @writer = writer

        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_green
      end

      # this is for the Report superclass, when we've detached from Java:
      def report_flush
        info ""

        if hasDifferences()
          print_file_names
          write_differences
        end
        clear()
      end

      def flush
        info "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!".on_green
        if hasDifferences()
          print_file_names
          write_differences
        end
        clear()
      end

      def print_file_names
        # only print file names once per report.
        # extend this for unified (file name per line)

        info "@from_file_name: #{@from_file_name}".yellow
        info "@to_file_name: #{@to_file_name}".yellow

        return if @from_file_name.nil? || @to_file_name.nil?
        
        sb = java.lang.StringBuilder.new
        sb.append @from_file_name
        sb.append " <=> "
        sb.append @to_file_name
        sb.append java.lang.System.getProperty("line.separator")
        
        begin
          @writer.write sb.toString()
        rescue java.io.IOException => ioe
          # nothing
        end
        
        @from_file_name = nil
        @to_file_name = nil
      end

      def reset from_file_name, from_contents, to_file_name, to_contents
        @from_file_name = from_file_name
        @to_file_name = to_file_name

        info "from_file_name: #{from_file_name}".cyan
        info "from_contents: #{from_contents}".cyan
        info "to_file_name: #{to_file_name}".cyan
        info "to_contents: #{to_contents}".cyan
      end
    end
  end
end
