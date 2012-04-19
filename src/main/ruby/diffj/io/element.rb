#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    class Element # < Pathname
      include Loggable

      attr_reader :source_version
      
      def initialize name, srcver
        info "name: #{name}".on_red
        info "name.class: #{name.class}".on_red
        @name = name
        @source_version = srcver
        @pn = Pathname.new name
      end

      def compare_to report, elmt
      end

      def compare_from_file report, file
      end

      def compare_from_dir report, dir
      end

      def basename
        @pn.basename
      end

      def children
        @pn.children
      end

      def file?
        @pn.file?
      end

      def directory?
        @pn.directory?
      end

      def to_s
        @pn.to_s
      end

      def to_str
        @pn.to_str
      end
    end
  end
end
