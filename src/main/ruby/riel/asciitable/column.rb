#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/log'

module RIEL
  module ASCIITable
    class Column
      attr_accessor :width
      attr_accessor :num
      attr_accessor :align
      attr_accessor :table

      def initialize table, num, width = nil, align = nil
        @table = table
        @num = num
        @width = width
        @align = align
      end

      def total fromrow, torow
        @table.cells_in_column(@num).inject(0) { |sum, cell| sum + (cell.row >= fromrow && cell.row <= torow ? cell.value.to_i : 0) }
      end
    end
  end
end
