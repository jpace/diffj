#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'
require 'diffj/io'
require 'diffj/fdiff/reports/short'
require 'diffj/fdiff/reports/long'
require 'diffj/fdiff/writers/ctx_hl'
require 'diffj/fdiff/writers/ctx_no_hl'
require 'diffj/fdiff/writers/no_context'

include Java

module DiffJ
  class Processor
    include Loggable
    
    attr_reader :exit_value
    attr_reader :report
    
    def initialize brief, context_opts, recurse, from_label, fromver, to_label, tover
      writer = $stdout
      @report = brief ? DiffJ::FDiff::Report::ShortReport.new(writer) : DiffJ::FDiff::Report::LongReport.new(writer, context_opts)
      @recurse = recurse
      @from_label = from_label
      @fromver = fromver
      @to_label = to_label
      @tover = tover
      @jef = DiffJ::IO::Factory.new
    end

    def create_java_element fname, label, source
      begin
        @jef.create_element Pathname.new(fname), label, source, @recurse
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
      from_elmt = create_from_element from_name
      return false if from_elmt.nil?
      from_elmt.compare_to @report, to_elmt
      true
    end
  end
end
