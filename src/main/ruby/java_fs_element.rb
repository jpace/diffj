#!/usr/bin/ruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'set'

include Java

# import ::Java::net.sourceforge.pmd.ast.ASTCompilationUnit
import org.incava.diffj.CompilationUnitDiff
import org.incava.diffj.DiffJException
import org.incava.diffj.JavaFile
import org.incava.diffj.JavaDirectory
import org.incava.analysis.Report

module Java
  module FS
    class Element < java.io.File
      include Loggable

      attr_reader :source_version

      def initialize name, srcver
        super(name)
        @name = name
        @source_version = srcver
      end

      def compare_to_xxx report, elmt
      end

      def compare_from_file_xxx report, file
      end

      def compare_from_dir_xxx report, dir
      end
    end

    class FileJRuby < JavaFile
      include Loggable
      
      class << self
        def create_file_xxx dir, otherElmt
          Log.info "dir: #{dir}"
          Log.info "otherElmt: #{otherElmt}"
          
          begin
            ::Java::FS::Factory.new.create_file(java.io.File.new(dir, otherElmt.getName()), nil, otherElmt.source_version);
          rescue DiffJException => de
            throw de
          rescue java.lang.Throwable => e
            throw DiffJException.new(e)
          end
        end

        def compare_xxx report, from_file, to_file
          begin
            from_file.compare report, to_file
          rescue java.lang.Throwable => e
            Log.info "e: #{e}".red
            e.printStackTrace()
            throw DiffJException.new(e)
          end
        end
      end

      def initialize file, label, contents, srcver
        super file, label, contents, srcver
        info "file: #{file}".bold
      end

      def compare_to_xxx report, to_elmt
        info "self: #{self}".bold
        info "to_elmt: #{to_elmt}".bold
        info "to_elmt.class: #{to_elmt.class}".bold
        info "to_elmt.class.class: #{to_elmt.class.class}".bold
        to_elmt.compare_from_file_xxx report, self
      end

      def compare_from_file_xxx report, from_file
        info "from_file: #{from_file}".bold
        info "from_file: #{from_file.class}".bold
        info "self: #{self}".bold
        info "self: #{self.class}".bold
        self.class.compare_xxx report, from_file, self
      end

      def compare_from_dir_xxx report, from_dir
        info "self: #{self}".magenta
        self.class.compare_xxx report, self.class.create_file_xxx(from_dir, self), self
      end
    end

    class DirectoryJRuby < JavaDirectory
      include Loggable

      def initialize file, srcver, recurse
        super(file, srcver, recurse)
        info "file: #{file}"
        @source_version = srcver
        @recurse = recurse
      end
      
      def create_java_file_xxx file, label
        FileJRuby.new file, label, nil, @source_version
      end

      def create_java_directory_xxx file
        DirectoryJRuby.new file, @source_version, @recurse
      end

      def element_xxx name
        files = listFiles()
        files.each do |file|
          if file.getName() == name
            return file.isDirectory() ? create_java_directory_xxx(file) : create_java_file_xxx(file, nil)
          end
        end
        nil
      end

      def compare_to_xxx report, to_elmt
        info "self: #{self}".bold
        info "to_elmt: #{to_elmt}".bold
        info "to_elmt.class: #{to_elmt.class}".bold
        info "to_elmt.class.class: #{to_elmt.class.class}".bold
        to_elmt.compare_from_dir_xxx report, self
      end

      def compare_from_file_xxx report, from_file
        info "self: #{self}".bold
        info "from_file: #{from_file}".bold
        FileJRuby.compare_xxx report, from_file, FileJRuby.create_file_xxx(self, from_file)
      end

      def compare_from_dir_xxx report, from_dir
        info "self: #{self}".bold
        names = Set.new
        names += from_dir.getElementNames()
        names += getElementNames()

        info "names: #{names}".yellow
        names.sort.each do |name|
          info "name: #{name}".yellow

          from_elmt = from_dir.element_xxx name
          info "from_elmt: #{from_elmt}".yellow
          info "from_elmt.class: #{from_elmt.class}".yellow
        
          to_elmt = element_xxx name
          info "to_elmt: #{to_elmt}".yellow
          info "to_elmt.class: #{to_elmt.class}".yellow

          if from_elmt && to_elmt && (from_elmt.isFile() || (from_elmt.isDirectory() && @recurse))
            from_elmt.compareTo report, to_elmt
          end
        end
      end
    end

    class Factory
      include Loggable
      
      def create_element file, label, source, recurseDirectories
        info "file: #{file}"
        javaFile = create_file file, label, source
        info "javaFile: #{javaFile}"
        if javaFile
          javaFile
        elsif file.isDirectory()
          DirectoryJRuby.new file, source, recurseDirectories
        else
          noSuchFile file, label
          nil
        end
      end

      def create_file file, label, source
        info "file: #{file}"
        if file.nil? || file.getName() == "-" || (file.isFile() && verifyExists(file, label))
          info "file: #{file}".cyan
          FileJRuby.new file, label, nil, source
        else
          nil
        end
      end

      def verifyExists file, label
        if file && file.exists()
          true
        else
          noSuchFile file, label
          false
        end
      end

      def noSuchFile file, label
        throw DiffJException.new(getName(file, label) + " does not exist")
      end

      def getName file, label
        label || file.getAbsolutePath()
      end
    end
  end
end
