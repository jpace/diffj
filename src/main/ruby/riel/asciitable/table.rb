#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/log'
require 'riel/asciitable/cell'
require 'riel/asciitable/column'
require 'riel/asciitable/row'

module RIEL
  module ASCIITable
    class TableData
      def keys
      end

      def fields
      end
      
      def value key, field
      end
    end

    class Table
      include Loggable      

      def initialize args
        @cells = Array.new
        @cellwidth = args[:cellwidth] || 12
        @align = args[:align] || :left
        @columns = Array.new
        @separator_rows = Hash.new
        @default_value = args[:default_value] || ""

        set_headings
        set_cells

        if sepival = args[:separators]
          add_separators sepival
        end

        totrow = args[:has_total_row]
        avgrow = args[:has_average_row]

        if totrow || avgrow
          add_separator_row '='
          if totrow
            TotalRow.new(self)
          end
          
          if avgrow
            AverageRow.new(self)
          end
        end
        
        if args[:has_total_columns]
          add_total_columns
        end
        
        if args[:highlight_max_cells]
          highlight_max_cells
        end
      end

      # sets a separator for the row preceding +rownum+. Does not change the
      # coordinates for any other cells.
      def set_separator_row rownum, char = '-'
        @separator_rows[rownum] = char
      end

      def add_separator_row char = '-'
        @separator_rows[last_row + 1] = char
      end

      def add_separators nbetween
        # banner every N rows
        drows = data_rows

        drows.first.upto((drows.last - 1) / nbetween) do |num|
          set_separator_row 1 + num * nbetween, '-'
        end
      end

      def last_column
        @cells.collect { |cell| cell.column }.max
      end

      def last_row
        @cells.collect { |cell| cell.row }.max
      end

      def cells_in_column col
        @cells.select { |cell| cell.column == col }
      end

      def cells_in_row row
        @cells.select { |cell| cell.row == row }
      end

      def cell col, row
        cl = @cells.detect { |c| c.row == row && c.column == col }
        unless cl
          cl = Cell.new(col, row, @default_value)
          @cells << cl
        end
        cl
      end

      def set_column_align col, align
        column(col).align = align
      end

      def set_column_width col, width
        column(col).width = width
      end

      def column_width col
        ((c = @columns[col]) && c.width) || @cellwidth
      end

      def column_align col
        ((c = @columns[col]) && c.align) || @align
      end

      def column col
        @columns[col] ||= Column.new(self, col, @cellwidth, @align)
      end

      def set_cellspan fromcol, tocol, row
        cell(fromcol, row).span = tocol
      end

      def set col, row, val
        cell(col, row).value = val
      end

      def set_color col, row, *colors
        cell(col, row).colors = colors
      end

      def print_row row, align = nil
        Row.new(self, row).print @columns, align
      end

      def print_banner char = '-'
        BannerRow.new(self, char).print
      end
      
      def print_header
        # cells in the header are always centered
        print_row 0, :center
        print_banner
      end

      def print
        print_header
        
        (1 .. last_row).each do |row|
          if char = @separator_rows[row]
            print_banner char
          end
          print_row row
        end
      end

      # returns the values in cells, sorted and unique
      def sort_values cells
        cells.collect { |cell| cell.value }.uniq.sort.reverse
      end

      def data_cells_for_row row, offset
        cells_for_row row, offset, fields.length * data_cell_span
      end

      def get_highlight_colors 
        Array.new
      end

      def highlight_cells_in_row row, offset
        cells = data_cells_for_row row, offset
        highlight_cells cells
      end

      def highlight_cells cells
        vals = sort_values cells

        colors = get_highlight_colors
        
        cells.each do |cell|
          idx = vals.index cell.value
          if cols = colors[idx]
            cell.colors = cols
          end
        end
      end

      def highlight_cells_in_column col
        cells = cells_in_column(col)[data_rows.first .. data_rows.last]
        highlight_cells cells
      end

      def highlight_max_cells
        (1 .. last_row).each do |row|
          (0 .. (data_cell_span - 1)).each do |offset|
            highlight_cells_in_row row, offset
          end
        end

        0.upto(1) do |n|
          highlight_cells_in_column data_columns.last + n
        end
      end

      def data_cell_span
        1
      end

      def set_headings
        cellspan = data_cell_span

        colidx = 0
        set colidx, 0, headings[0]

        colidx += 1

        (1 ... headings.length).each do |hi|
          set colidx, 0, headings[hi]
          if cellspan > 1
            set_cellspan colidx, colidx - 1 + cellspan, 0
          end
          colidx += cellspan
        end
      end

      def add_total_columns
        last_data_col = data_columns.last
        totcol = last_data_col + 1

        (1 .. last_row).each do |row|
          (0 .. (data_cell_span - 1)).each do |offset|
            rowcells = cells_for_row row, offset, last_data_col
            total = rowcells.collect { |cell| cell.value }.inject(0) { |sum, num| sum + num }
            set totcol + offset, row, total
          end
        end
      end

      # returns the cells up to the given column
      def cells_for_row row, offset, maxcol
        cells = cells_in_row row
        cells = cells[1 .. maxcol]
        cells.select_with_index { |cell, cidx| (cidx % data_cell_span) == offset }
      end    

      def set_cells
        rownum = 1
        keys.each_with_index do |name, nidx|
          set 0, rownum, name
          
          colidx = 1
          fields.each do |field|
            (0 .. (data_cell_span - 1)).each do |didx|
              val = value name, field, didx
              set colidx, rownum, val
              colidx += 1
            end
          end
          rownum += 1
        end
      end

      def data
        nil
      end

      def keys
        data.keys
      end

      def fields
        data.fields
      end

      def value key, field, index
        data.value key, field, index
      end

      def data_rows
        (1 .. keys.length)
      end

      def data_columns
        (1 .. fields.length * data_cell_span)
      end
    end
  end
end
