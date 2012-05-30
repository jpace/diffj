#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/fdiff/reports/report'
require 'diffj/fdiff/writers/ctx_hl'
require 'diffj/fdiff/writers/ctx_no_hl'
require 'diffj/fdiff/writers/no_context'

include DiffJ::FDiff::Writer

# Reports differences in long form.
module DiffJ::FDiff::Report
  class LongReport < BaseReport
    def initialize writer, context_opts = Hash.new
      super writer

      @context_opts = context_opts
      @from_contents = nil
      @to_contents = nil
    end

    def write_differences
      from_lines = @from_contents.split "\n"
      to_lines = @to_contents.split "\n"

      dw = if @context_opts && @context_opts[:context]
             if @context_opts[:highlight]
               from_color = @context_opts[:from_color]
               to_color = @context_opts[:to_color]
               ContextHighlightWriter.new from_lines, to_lines, from_color, to_color
             else
               ContextNoHighlightWriter.new from_lines, to_lines
             end
           else
             NoContextWriter.new from_lines, to_lines
           end
      
      differences.each do |fdiff|
        str = dw.difference fdiff
        @writer.write str
      end
      
      @writer.flush
      # we can't close STDOUT:
      # writer.close
    end

    def reset from_file_name, from_contents, to_file_name, to_contents
      @from_contents = from_contents
      @to_contents = to_contents
      super
    end
  end
end
