#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'
require 'diffj/io'
require 'diffj/analysis/short_report'
require 'diffj/analysis/long_report'
require 'diffj/io/diffwriter/context'
require 'diffj/io/diffwriter/context_highlight'
require 'diffj/io/diffwriter/no_context'

include Java

java_import org.incava.diffj.Options
java_import org.incava.diffj.DiffJException
java_import org.incava.analysis.BriefReport
java_import org.incava.analysis.DetailedReport

Log::level = Log::DEBUG
Log.set_widths(-15, 5, -50)

module DiffJ
  class CLI
    include Loggable
    
    attr_reader :exit_value
    attr_reader :report
    
    def initialize brief, context, highlight, recurse, from_label, fromver, to_label, tover
      writer = java.io.OutputStreamWriter.new java.lang.System.out
      @report = brief ? DiffJ::Analysis::ShortReport.new(writer) : DiffJ::Analysis::LongReport.new(writer, context, highlight)
      @recurse = recurse
      @from_label = from_label
      @fromver = fromver
      @to_label = to_label
      @tover = tover
      @jef = DiffJ::IO::Factory.new
      @exit_value = 0
    end

    def create_java_element fname, label, source
      begin
        info "fname: #{fname}"
        @jef.create_element java.io.File.new(fname), label, source, @recurse
      rescue StandardError => de
        $stderr.puts de.message
        @exit_value = 1
        nil
      end
    end
    
    def create_to_element to_name
      create_java_element to_name, @to_label, @tover
    end
    
    def create_from_element from_name
      create_java_element from_name, @from_label, @fromver
    end

    def compare from_name, to_elmt
      begin 
        from_elmt = create_from_element from_name
        return false if from_elmt.nil?
        from_elmt.compare_to @report, to_elmt
        @exit_value = @report.differences.was_added? ? 1 : 0
        true
      rescue DiffJException => de
        $stderr.puts de.message
        @exit_value = 1
        nil
      end
    end

    def process_names names
      if names.size < 2
        $stderr.puts "usage: diffj from-file to-file"
        exit_value = 1
        return
      end

      return unless to_elmt = create_to_element(names[-1])
      names[0 ... -1].each do |fromname|
        compare fromname, to_elmt
      end
    end
  end
end

if __FILE__ == $0
  opts = Options.get
  names = opts.process ARGV
  
  diffj = DiffJ::CLI.new(opts.showBriefOutput, 
                         opts.showContextOutput, 
                         opts.highlightOutput,
                         opts.recurse,
                         opts.firstFileName, opts.getFromSource,
                         opts.getSecondFileName, opts.getToSource)
  
  rarray = Array.new
  names.each do |name|
    rarray << name
  end

  diffj.process_names rarray
  puts "exiting with value: #{diffj.exit_value}"
  exit diffj.exit_value
end
