#!/usr/bin/jruby -w
# -*- ruby -*-

module DiffJ
  module IO
    # location within a file (line and column)
    class Location
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

      def inspect
        to_s
      end

      def eql? other
        (self <=> other) == 0
      end

      def == other
        (self <=> other) == 0
      end

      def <=> other
        (line <=> other.line).nonzero? || (column <=> other.column).nonzero? || 0
      end
    end
  end
end
