#!/usr/bin/jruby -w
# -*- ruby -*-

require 'set'
require 'diffj/ast'
require 'diffj/io/element'
require 'diffj/io/file'

module DiffJ
  module IO
    class Directory < Element
      def initialize file, srcver, recurse
        super file.to_s, srcver
        info "file: #{file}"
        @recurse = recurse
      end
      
      def create_java_file jifile, label
        File.new jifile, label, nil, source_version
      end

      def create_java_directory jidir
        Directory.new jidir, source_version, @recurse
      end

      def create_element jifd
        return jifd.directory? ? create_java_directory(jifd) : create_java_file(jifd, nil)
      end

      # def + name
      #   pn = Pathname.new(self.to_s) + name
      #   pn.directory? Directory.new(pn.to_s) : File.n
      # end
        
      def element_names
        files = subelements
        names = Array.new
        files && files.each do |file|
          if file.directory? || (file.file? && file.to_s.index(%r{\.java$}))
            names << file.basename
          end
        end
        names
      end

      def element name
        files = subelements
        files.each do |file|
          if file.basename == name
            return create_element file
          end
        end
        nil
      end

      def subelements
        # files = listFiles # children
        # children.collect { |f| Pathname.new(f.to_s) }
        children
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
