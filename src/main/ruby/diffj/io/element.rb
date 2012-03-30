#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    class Element < java.io.File
      include Loggable

      attr_reader :source_version

      def initialize name, srcver
        super(name)
        info "name: #{name}"
        @name = name
        @source_version = srcver
      end

      def compare_to report, elmt
      end

      def compare_from_file report, file
      end

      def compare_from_dir report, dir
      end
    end
  end
end
