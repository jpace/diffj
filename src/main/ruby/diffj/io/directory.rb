#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'set'
require 'diffj/ast'
require 'diffj/io/element'
require 'diffj/io/file'

include Java

import org.incava.diffj.DiffJException

module DiffJ
  module IO
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
  end
end
