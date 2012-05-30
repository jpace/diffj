#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'optparse'
require 'riel/env'
require 'diffj/fdiff/writers/ctx_hl'

include Java

module DiffJ
  class Options < OptionParser
    include Loggable

    SOURCE_DEFAULT = "1.5"

    DEFAULT_FROM_COLOR_TEXT = DiffJ::FDiff::Writer::ContextHighlightWriter::DEFAULT_FROM_COLOR_TEXT
    DEFAULT_TO_COLOR_TEXT = DiffJ::FDiff::Writer::ContextHighlightWriter::DEFAULT_TO_COLOR_TEXT

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
    attr_reader :from_color
    attr_reader :to_color

    def initialize
      @show_brief_output = false
      @show_context_output = false
      @highlight_output = false
      @from_source = SOURCE_DEFAULT
      @to_source = SOURCE_DEFAULT

      @from_color = DEFAULT_FROM_COLOR_TEXT
      @to_color = DEFAULT_TO_COLOR_TEXT

      @recurse = false
      @first_file_name = nil
      @second_file_name = nil
      @show_version = false
      @show_help = false
      @verbose = false

      homedir = Env::home_directory
      @rcfile = homedir && Pathname.new(homedir) + '.diffjrc'
      
      super do |op|
        op.banner = "Usage: diffj [options] from-file to-file"
        op.separator ""
        op.separator "Options"

        op.on "--brief", "Display output in brief form" do |@show_brief_output|
        end

        op.on "--context", "Show context (non-brief form only)" do |@show_context_output|
          @show_brief_output = false
          @highlight_output = true
        end
        
        op.on "--[no-]highlight", "Use colors (context output only)" do |@highlight_output|
          if @highlight_output
            @show_brief_output = false
          end
        end

        op.on "--recurse", "Process directories recursively" do |@recurse|
        end

        op.on "--from-source VERSION", "The Java source version of from-file (default: #{SOURCE_DEFAULT})" do |@from_source|
        end
        
        op.on "--to-source VERSION", "The Java source version of to-file (default: #{SOURCE_DEFAULT})" do |@to_source|
        end

        op.on "--from-color COLOR", "The text color of the from-file text (default: #{DEFAULT_FROM_COLOR_TEXT})" do |@from_color|
        end
        
        op.on "--to-color COLOR", "The text color of the to-file text (default: #{DEFAULT_TO_COLOR_TEXT})" do |@to_color|
        end

        op.on "--source VERSION", "The Java source version of from-file and to-file (default: #{SOURCE_DEFAULT})" do |src|
          @from_source = src
          @to_source = src
        end

        op.on "-u", "Output unified context. Unused; for compatibility with GNU diff" do
          # ignored
        end

        op.on "-L", "--name NAME", "Set the first/second name to be displayed" do |name|
          if @first_file_name
            @second_file_name = name
          else
            @first_file_name = name
          end
        end

        op.on "--verbose", "Run in verbose mode (for debugging)" do |@verbose|
        end
        
        op.on_tail "-h", "--help", "Show this message" do |@show_help|
        end
        
        op.on_tail "-v", "--version", "Display the version" do |@show_version|
        end
      end
    end

    def parse_from_rcfile rcfile
      asopts = Array.new

      ::IO.readlines(rcfile).each do |line|
        line = line.chomp.strip.gsub %r{\#.*}, ''
        name, value = line.split(%r{\s*[:=]\s*})

        if value == "true"
          asopts << "--#{name}"
        elsif value == "false"
          asopts << "--no-#{name}" << value
        else
          asopts << "--#{name}" << value
        end
      end

      parse! asopts
    end

    def process args
      if @rcfile && Pathname.new(@rcfile).exist?
        parse_from_rcfile @rcfile
      end
      parse! args
    end
  end
end
