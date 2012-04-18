#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module FDiff
    module Report
      class BaseReport
        include Loggable
        
        def initialize writer
          @writer = writer
          @differences = SortedSet.new
        end

        def get_differences
          @differences
        end

        def differences
          @differences
        end

        def clear
          info "differences: #{differences.inspect}".yellow
          @had_differences ||= !@differences.empty?
          @differences.clear
          info "differences: #{differences.inspect}".yellow
          info "differences: #{differences.size}".yellow
        end

        def has_differences?
          !@differences.empty?
        end
        
        def had_differences?
          @had_differences
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
          
          @writer.write str
          
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
end
