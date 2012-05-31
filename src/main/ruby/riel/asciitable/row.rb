#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/log'

module RIEL
  module ASCIITable
    class Row
      include Loggable
      
      attr_accessor :table
      attr_accessor :num

      def initialize table, num
        @table = table
        @num = num
      end

      def print columns, align = nil
        tocol = @table.last_column
        col = 0
        fmtdvalues = Array.new
        while col <= tocol
          aln = align || @table.column_align(col)
          cell = @table.cell(col, @num)
          width = @table.column_width col
          fmtdvalues << cell.formatted_value(width, aln)
          if cell.span
            col += (cell.span - col)
          end
          col += 1
        end
        print_cells fmtdvalues
      end

      def print_cells values
        $stdout.puts "| " + values.join(" | ") + " |"
      end
    end

    class BannerRow < Row
      def initialize table, char
        @char = char
        super(table, nil)
      end

      def print
        banner = (0 .. @table.last_column).collect { |col| bc = BannerCell.new(@char, col, 1) }
        bannervalues = banner.collect_with_index do |bc, col| 
          width = @table.column_width col
          bc.formatted_value width, :center
        end
        print_cells bannervalues
      end
    end

    class StatRow < Row
      def initialize table
        firstdatarow = table.data_rows.first
        lastdatarow = table.data_rows.last
        statrownum = table.last_row + 1

        nrows = lastdatarow + 1 - firstdatarow
        
        super table, statrownum
        
        ncolumns = @table.last_column
        
        @table.set 0, statrownum, name
        
        1.upto(ncolumns - 1) do |col|
          val = calculate col, firstdatarow, lastdatarow
          @table.set col, statrownum, val
        end
      end

      def name
      end

      def calculate col, firstdatarow, lastdatarow
      end
    end

    class TotalRow < StatRow
      def name
        "total"
      end

      def calculate col, fromrow, torow
        @table.column(col).total fromrow, torow
      end
    end

    class AverageRow < StatRow
      def name
        "average"
      end
      
      def calculate col, fromrow, torow
        nrows = torow + 1 - fromrow
        total = @table.column(col).total fromrow, torow
        avg = total / nrows
      end
    end
  end
end
