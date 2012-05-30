#!/usr/bin/jruby -w
# -*- ruby -*-

require 'riel/log'
require 'diffj/app/processor'
require 'diffj/app/options'
require 'diffj/util/exception'

Log.set_widths(-15, 5, -50)

module DiffJ
  class CLI < Processor
    include Loggable

    VERSION = "1.3.0"

    class << self 
      def run args = ARGV
        Log.info "args: #{args}"

        opts = DiffJ::Options.new
        names = opts.process args

        if opts.verbose
          Log::level = Log::DEBUG
        end

        if opts.show_version
          puts "diffj, version #{VERSION}"
          puts "Written by Jeff Pace (jeugenepace [at] gmail [dot] com)"
          puts "Released under the Lesser GNU Public License"
          return 1
        end

        if opts.show_help
          puts opts
          return 1
        end

        ctx_opts = { 
          :context => opts.show_context_output, 
          :highlight => opts.highlight_output,
          :from_color => opts.from_color,
          :to_color => opts.to_color
        }

        diffj = new(opts.show_brief_output, 
                    ctx_opts,
                    opts.recurse,
                    opts.first_file_name, opts.from_source,
                    opts.second_file_name, opts.to_source)

        diffj.process_names names

        Log.info "diffj.exit_value: #{diffj.exit_value}"
        diffj.exit_value
      end
    end
    
    attr_reader :exit_value
    attr_reader :report
    
    def initialize brief, context_opts, recurse, from_label, fromver, to_label, tover
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
      rescue DiffJ::Exception => de
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
