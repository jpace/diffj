#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'
require 'diffj/app/processor'
require 'diffj/app/options'

include Java

java_import org.incava.diffj.DiffJException

Log::level = Log::DEBUG
Log.set_widths(-15, 5, -50)

module DiffJ
  class CLI < Processor
    include Loggable

    class << self 
      def run args = ARGV
        Log.info "args: #{args}"

        opts = DiffJ::Options.new
        names = opts.process args

        Log.info "names: #{names}".bold.yellow

        diffj = new(opts.show_brief_output, 
                    opts.show_context_output, 
                    opts.highlight_output,
                    opts.recurse,
                    opts.first_file_name, opts.from_source,
                    opts.second_file_name, opts.to_source)
        
        rarray = Array.new
        names.each do |name|
          rarray << name
        end

        diffj.process_names rarray
        Log.info "diffj.exit_value: #{diffj.exit_value}"
        diffj.exit_value
      end
    end
    
    attr_reader :exit_value
    attr_reader :report
    
    def initialize brief, context, highlight, recurse, from_label, fromver, to_label, tover
      super
      @exit_value = 0
    end
    
    def create_to_element to_name
      create_java_element to_name, @to_label, @tover
    end
    
    def create_from_element from_name
      create_java_element from_name, @from_label, @fromver
    end

    def compare from_name, to_elmt
      begin 
        if super
          @exit_value = @report.had_differences? ? 1 : 0
          true
        end
      rescue DiffJException => de
        $stderr.puts de.message
        @exit_value = 1
        nil
      end
    end

    def process_names names
      if names.size < 2
        $stderr.puts "usage: diffj from-file to-file"
        @exit_value = 1
        return
      end

      return unless to_elmt = create_to_element(names[-1])
      names[0 ... -1].each do |fromname|
        compare fromname, to_elmt
      end
    end
  end
end
