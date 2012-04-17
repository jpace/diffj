#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/fdiffset'

include Java

module DiffJ
  module Analysis
    # Reports differences form.
    class Report
      include Loggable
      
      def initialize writer
        # super writer
        @writer = writer
        @differences = DiffJ::FDiff::FDiffSet.new
      end

      def get_differences
        @differences
      end

      def differences
        @differences
      end

      def clear
        info "differences: #{differences}".yellow
        info "differences: #{differences.inspect}".yellow
        @differences.clear
        info "differences: #{differences.inspect}".yellow
        info "differences: #{differences.size}".yellow
      end

      def has_differences?
        !@differences.empty?
      end
      
      def flush
        if has_differences?
          print_file_names
          write_differences
        end
        clear
      end

      def print_file_names
        # only print file names once per report.
        # extend this for unified (file name per line)

        return if @from_file_name.nil? || @to_file_name.nil?
        
        str = ""
        str << @from_file_name
        str << " <=> "
        str << @to_file_name
        str << java.lang.System.getProperty("line.separator")
        
        begin
          @writer.write str
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
