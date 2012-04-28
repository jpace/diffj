#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'java'
require 'riel'
require 'optparse'

include Java

module DiffJ
  class Options
    include Loggable

    SOURCE_DEFAULT = "1.5"

    attr_reader :show_brief_output
    attr_reader :show_context_output
    attr_reader :highlight_output
    attr_reader :from_source
    attr_reader :to_source
    attr_reader :recurse
    attr_reader :first_file_name
    attr_reader :second_file_name
    attr_reader :show_help
    attr_reader :show_version
    attr_reader :verbose

    def initialize
      @show_brief_output = false
      @show_context_output = false
      @highlight_output = false
      @from_source = SOURCE_DEFAULT
      @to_source = SOURCE_DEFAULT
      @recurse = false
      @first_file_name = nil
      @second_file_name = nil
      @show_version = false
      @show_help = false
      @verbose = false
    end

    def process args
      options = OptionParser.new do |op|
        op.banner = "Usage: diffj [options] from-file to-file"
        op.separator ""
        op.separator "Options"

        op.on "--brief", "Display output in brief form" do |@show_brief_output|
        end

        op.on "--context", "Show context (non-brief form only)" do |@show_context_output|
          @show_brief_output = false
          @highlight_output = true
        end
        
        op.on "--highlight", "Use colors (context output only)" do |@highlight_output|
          @show_brief_output = false
        end

        op.on "--recurse", "Process directories recursively" do |@recurse|
        end

        op.on "--from-source VERSION", "The Java source version of from-file (default is #{SOURCE_DEFAULT}" do |@from_source|
        end

        op.on "--to-source VERSION", "The Java source version of to-file (default is #{SOURCE_DEFAULT}" do |@to_source|
        end

        op.on "--source VERSION", "The Java source version of from-file and to-file (default is #{SOURCE_DEFAULT}" do |src|
          @from_source = src
          @to_source = src
        end

        op.on "-u", "Output unified context. Unused; for compatibility with GNU diff" do
          # ignored
        end

        op.on "-L", "--name NAME", "Set the first/second name to be displayed" do |name|
          info "name: #{name}".red

          if @first_file_name
            @second_file_name = name
          else
            @first_file_name = name
          end
        end

        op.on "--verbose", "Run in verbose mode (for debugging)" do |@verbose|
        end

        op.on_tail "-h", "--help", "Show this message" do |@show_help|
          info "@show_help: #{@show_help}".bold
        end

        op.on_tail "-v", "--version", "Display the version" do |@show_version|
        end
        
        args = op.parse! args
      end

      args
    end
  end
end
