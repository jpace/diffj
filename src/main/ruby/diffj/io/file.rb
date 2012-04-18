#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'set'
require 'diffj/ast'
require 'diffj/io/element'
require 'diffj/io/factory'

include Java

import org.incava.diffj.DiffJException
import ::Java::net.sourceforge.pmd.ast.TokenMgrError
import ::Java::net.sourceforge.pmd.ast.ParseException

module DiffJ
  module IO
    class File < Element
      include Loggable
      
      class << self
        def create_file dir, other_elmt
          Log.info "dir: #{dir}"
          Log.info "other_elmt: #{other_elmt}"
          
          begin
            ::DiffJ::IO::Factory.new.create_file java.io.File.new(dir, other_elmt.name), nil, other_elmt.source_version
          rescue DiffJException => de
            raise de
          rescue => e
            raise DiffJException.new e
          end
        end

        def compare report, from_file, to_file
          from_file.compare report, to_file
          report.flush
        end
      end

      attr_reader :label
      attr_reader :file
      attr_reader :contents
      attr_reader :srcver

      def initialize file, label, contents, srcver
        super label || file.path, srcver
        info "file: #{file}".bold
        @file = file
        @label = label
        @contents = contents
        @srcver = srcver

        begin
          is_stdin = file.nil? || file.name == "-"
          @contents = contents
          read if @contents.nil?
          @label = label || (is_stdin ? "-" : file.getPath)
        rescue java.io.FileNotFoundException => e
          raise DiffJException.new "Error opening file '" + file.absolute_path + "': " + e.message, e
        rescue java.io.IOException => e
          raise DiffJException.new "I/O error with file '" + file.absolute_path + "': " + e.message, e
        end
      end

      def read
        is_stdin = @file.nil? || @file.name == "-"
        lines = is_stdin ? $stdin.readlines : ::IO.readlines(@file.to_s)
        @contents = lines.join
      end

      def compare_to report, to_elmt
        info "self: #{self}".bold
        info "to_elmt: #{to_elmt}".bold
        to_elmt.compare_from_file report, self
      end

      def compare_from_file report, from_file
        info "from_file: #{from_file}".bold
        info "self: #{self}".bold
        self.class.compare report, from_file, self
      end

      def compare_from_dir report, from_dir
        info "self: #{self}".magenta
        self.class.compare report, self.class.create_file(from_dir, self), self
      end

      def get_parser 
        reader = java.io.StringReader.new @contents
        jcs    = ::Java::net.sourceforge.pmd.ast.JavaCharStream.new reader
        parser = ::Java::net.sourceforge.pmd.ast.JavaParser.new jcs

        case @srcver
        when "1.3"
          parser.setJDK13
        when "1.5", "1.6"
          # no setJDK16 yet in PMD
          parser.setJDK15
        when "1.4"
          # currently the default in PMD
        else
          raise DiffJException.new("source version '" + @srcver + "' not recognized")
        end

        info "parser: #{parser}"
        parser
      end
      
      def compile
         begin
           parser = get_parser
           parser.CompilationUnit
         rescue TokenMgrError => tme
           raise DiffJException.new("Error tokenizing " + @label + ": " + tme.message)
         rescue ParseException => pe
           raise DiffJException.new("Error parsing " + @label + ": " + pe.message)
         rescue java.lang.Throwable => e
           raise DiffJException.new("Error processing " + @label + ": " + e.message)
         end
      end
      
      def compare report, to_file
        from_comp_unit = compile
        to_comp_unit   = to_file.compile

        # it looks like JRuby has a limit of three method parameters for lookup,
        # so we do it ourself here:
        method_params = [ java.lang.String, java.lang.String, java.lang.String, java.lang.String ]
        # report.java_send :reset, method_params, label, contents, to_file.label, to_file.contents

        report.reset label, contents, to_file.label, to_file.contents

        cud = ::DiffJ::CompUnitComparator.new report
        cud.compare from_comp_unit, to_comp_unit
      end
    end
  end
end
