#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/report'
require 'diffj/io/writer'

include Java

java_import org.incava.analysis.DiffContextHighlightWriter
java_import org.incava.analysis.DiffContextWriter
java_import org.incava.analysis.DiffNoContextWriter

module DiffJ
  module Analysis
    # Reports differences in long form.
    class LongReport < Report
      include Loggable
      
      def initialize writer, show_context, highlight
        super writer

        info "DiffJ::IO::CtxHighltWriter: #{DiffJ::IO::Diff::CtxHighltWriter}".on_green

        @dwcls = show_context ? (highlight ?  DiffJ::IO::Diff::CtxHighltWriter : DiffContextWriter) : DiffNoContextWriter
        @from_contents = nil
        @to_contents = nil
      end

      def write_differences
        begin
          from_lines = @from_contents.split "\n"
          to_lines = @to_contents.split "\n"
          dw = @dwcls.new from_lines, to_lines
          
          differences.each do |fdiff|
            str = dw.getDifference fdiff
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
