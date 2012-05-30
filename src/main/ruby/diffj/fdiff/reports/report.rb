#!/usr/bin/jruby -w
# -*- ruby -*-

require 'riel/log'

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
          @had_differences ||= !@differences.empty?
          @differences.clear
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
          str << EOLN
          
          @writer.write str
          
          @from_file_name = nil
          @to_file_name = nil
        end

        def reset from_file_name, from_contents, to_file_name, to_contents
          @from_file_name = from_file_name
          @to_file_name = to_file_name
        end
      end
    end
  end
end
