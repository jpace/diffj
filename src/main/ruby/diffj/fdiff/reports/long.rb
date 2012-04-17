#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/fdiff/reports/report'
require 'diffj/fdiff/writers/context'
require 'diffj/fdiff/writers/context_highlight'
require 'diffj/fdiff/writers/no_context'

include Java

include DiffJ::FDiff::Writer

module DiffJ
  module Analysis
    # Reports differences in long form.
    class LongReport < Report
      include Loggable
      
      def initialize writer, show_context, highlight
        super writer

        @dwcls = show_context ? (highlight ?  ContextHighlightWriter : ContextWriter) : NoContextWriter
        @from_contents = nil
        @to_contents = nil
      end

      def write_differences
        begin
          from_lines = @from_contents.split "\n"
          to_lines = @to_contents.split "\n"
          dw = @dwcls.new from_lines, to_lines
          
          differences.each do |fdiff|
            str = dw.difference fdiff
            @writer.write(str)
          end

          @writer.flush
          # we can't close STDOUT:
          # writer.close
        rescue java.io.IOException => ioe
          # nothing
        end
      end

      def reset from_file_name, from_contents, to_file_name, to_contents
        @from_contents = from_contents
        @to_contents = to_contents
        super
      end
    end
  end
end
