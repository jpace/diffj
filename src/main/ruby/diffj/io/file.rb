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
            ::DiffJ::IO::Factory.new.create_file(java.io.File.new(dir, other_elmt.getName()), nil, other_elmt.source_version)
          rescue DiffJException => de
            raise de
          rescue java.lang.Throwable => e
            raise DiffJException.new(e)
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
        super(label || file.path, srcver)
        info "file: #{file}".bold
        @file = file
        @label = label
        @contents = contents
        @srcver = srcver

        begin
          isStdin = file.nil? || file.getName() == "-"
          @contents = contents
          unless @contents
            reader = isStdin ? java.io.FileReader.new(java.io.FileDescriptor.in) : java.io.FileReader.new(file)
            @contents = org.incava.ijdk.io.ReaderExt.readAsString(reader, java.util.EnumSet.of(org.incava.ijdk.io.ReadOptionType::ADD_EOLNS))
          end
          @label = label || (isStdin ? "-" : file.getPath())
        rescue java.io.FileNotFoundException => e
          raise DiffJException.new "Error opening file '" + file.getAbsolutePath() + "': " + e.getMessage(), e
        rescue java.io.IOException => e
          raise DiffJException.new "I/O error with file '" + file.getAbsolutePath() + "': " + e.getMessage(), e
        end
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
          parser.setJDK13()
        when "1.5", "1.6"
          # no setJDK16 yet in PMD
          parser.setJDK15()
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
           parser.CompilationUnit()
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
        
        report.reset label, contents, to_file.label, to_file.contents
        
        cud = ::DiffJ::CompUnitDiff.new report
        cud.compare from_comp_unit, to_comp_unit
      end
    end

    class Directory < Element
      include Loggable

      def initialize file, srcver, recurse
        super(file.path, srcver)
        info "file: #{file}"
        @recurse = recurse
      end
      
      def create_java_file file, label
        File.new file, label, nil, source_version
      end

      def create_java_directory file
        self.class.new file, source_version, @recurse
      end

      def element_names
        files = listFiles
        names = Array.new
        files && files.each do |file|
          names << file.name if file.directory? || (file.file? && file.name.index(%r{\.java$}))
        end
        names
      end

      def element name
        files = listFiles
        files.each do |file|
          if file.name == name
            return file.directory ? create_java_directory(file) : create_java_file(file, nil)
          end
        end
        nil
      end

      def compare_to report, to_elmt
        info "self: #{self}"
        info "to_elmt: #{to_elmt}"
        to_elmt.compare_from_dir report, self
      end

      def compare_from_file report, from_file
        info "self: #{self}"
        info "from_file: #{from_file}"
        File.compare report, from_file, File.create_file(self, from_file)
      end

      def compare_from_dir report, from_dir
        info "self: #{self}"
        names  = Set.new
        names += from_dir.element_names
        names += element_names

        names.sort.each do |name|
          info "name: #{name}"
          from_elmt = from_dir.element name
          info "from_elmt: #{from_elmt}"
          to_elmt = element name
          info "to_elmt: #{to_elmt}"
          if from_elmt && to_elmt && (from_elmt.file? || (from_elmt.directory? && @recurse))
            from_elmt.compare_to report, to_elmt
          end
        end
      end
    end

    class Factory
      include Loggable
      
      def create_element file, label, source, recurse
        info "file: #{file}"
        javafile = create_file file, label, source
        if javafile
          javafile
        elsif file.directory?
          Directory.new file, source, recurse
        else
          no_such_file file, label
          nil
        end
      end

      def create_file file, label, source
        if file.nil? || file.name == "-" || (file.file? && verify_exists(file, label))
          File.new file, label, nil, source
        else
          nil
        end
      end

      def verify_exists file, label
        if file && file.exists
          true
        else
          no_such_file file, label
          false
        end
      end

      def no_such_file file, label
        raise DiffJException.new(name(file, label) + " does not exist")
      end

      def name file, label
        label || file.absolute_path
      end
    end
  end
end
