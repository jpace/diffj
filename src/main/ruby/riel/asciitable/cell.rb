#!/usr/bin/ruby -w
# -*- ruby -*-

require 'riel/log'

module RIEL
  class Cell
    include Loggable
    
    attr_reader :column
    attr_reader :row

    attr_accessor :value
    attr_accessor :colors
    attr_accessor :span

    def initialize column, row, value = nil, colors = Array.new
      @column = column
      @row = row
      @value = value
      @colors = colors
      @span = span
    end

    def _value width
      value.nil? ? "" : value.to_s
    end

    def inspect
      "(#{@column}, #{@row}) => #{@value}"
    end

    def to_s
      "(#{@column}, #{@row}) => #{@value}"
    end

    def formatted_value width, align
      strval = _value width

      if @span
        ncolumns = @span - @column
        width = width * (1 + ncolumns) + (3 * ncolumns)
      end

      diff = width - strval.length
        
      lhs, rhs = case align
                 when :left
                   [ 0, diff ]
                 when :right
                   [ diff, 0 ]
                 when :center
                   l = diff / 2
                   r = diff - l
                   [ l, r ]
                 else
                   $stderr.puts "oh my!: #{align}"
                 end

      str = (" " * lhs) + strval + (" " * rhs)
      
      if colors
        colors.each do |cl|
          str = str.send cl
        end
      end

      str
    end
  end

  class BannerCell < Cell
    def initialize char, col, row
      @char = char
      super(col, row)
    end

    def _value width
      @char * width
    end
  end  
end
