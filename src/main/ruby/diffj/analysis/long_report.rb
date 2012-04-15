#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/analysis/report'

include Java

java_import org.incava.analysis.Report
java_import org.incava.analysis.DiffContextHighlightWriter
java_import org.incava.analysis.DiffContextWriter
java_import org.incava.analysis.DiffNoContextWriter

module DiffJ
  module Analysis
    # Reports differences in long form.
    class LongReport < Rpt
      include Loggable
      
      def initialize writer, show_context, highlight
        super(writer)

        @show_context = show_context
        @highlight = highlight
        @from_contents = nil
        @to_contents = nil
      end

      def write_differences
        begin
          diffs = getDifferences()
          info "flushing diffs".on_yellow

          writer = getWriter()
          info "writer: #{writer}".on_yellow

          info "@from_contents: #{@from_contents}".red
          info "@to_contents: #{@to_contents}".green

          from_lines = @from_contents.split "\n"
          to_lines = @to_contents.split "\n"

          dwcls = (@show_context ? (@highlight ?  DiffContextHighlightWriter : DiffContextWriter) : DiffNoContextWriter)

          dw = dwcls.new from_lines, to_lines
          
          diffs.each do |fdiff|
            str = dw.getDifference(fdiff)
            writer.write(str)
          end

          writer.flush()
                
          # we can't close STDOUT:
          # writer.close();
        rescue java.io.IOException => ioe
          # nothing
        end
      end

      def reset from_file_name, from_contents, to_file_name, to_contents
        @from_file_name = from_file_name
        @to_file_name = to_file_name

        # rpt.report_reset
        info "from_file_name: #{from_file_name}".cyan
        info "from_contents: #{from_contents}".cyan
        info "to_file_name: #{to_file_name}".cyan
        info "to_contents: #{to_contents}".cyan
        
        @from_contents = from_contents
        @to_contents = to_contents

        super
      end
    end
  end
end
