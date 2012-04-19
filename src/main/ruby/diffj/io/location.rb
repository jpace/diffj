#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module IO
    # location within a file (line and column)
    class Location
      include Loggable      

      class << self
        def beginning tk
          tk && new(tk.beginLine, tk.beginColumn)
        end
    
        def ending tk
          tk && new(tk.endLine, tk.endColumn)
        end
      end

      attr_reader :line
      attr_reader :column

      def initialize line, column
        @line = line
        @column = column
      end

      def to_s
        "#{line}:#{column}"
      end

      def eql? other
        (self <=> other) == 0
      end

      def == other
        (self <=> other) == 0
      end

      def <=> other
        cmp = line <=> other.line
        cmp.nonzero? || column <=> other.column
      end
    end
  end
end
