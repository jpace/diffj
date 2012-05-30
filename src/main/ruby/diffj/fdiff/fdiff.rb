#!/usr/bin/jruby -w
# -*- ruby -*-

require 'riel/log'
require 'java'
require 'diffj/io/location'
require 'diffj/io/locrange'

include Java

module DiffJ
  module FDiff
    module ClassMethods
      def to_begin_location tk
        DiffJ::IO::Location.beginning tk
      end
    
      def to_end_location tk
        DiffJ::IO::Location.ending tk
      end      

      def tks_to_location_range from, to = from
        DiffJ::IO::LocationRange.new to_begin_location(from), to_end_location(to)
      end

      def locs_to_location_range from, to
        from && DiffJ::IO::LocationRange.new(from, to)
      end

      # handles legacy overloading of FDiff* parameters for tokens, locations,
      # and location ranges.
      def convert_to_locations args
        if args.size == 1
          arg = args[0]
          if locs = arg[:locations]
            locs.each_slice(2).to_a.collect do |locpair|
              locs_to_location_range(*locpair)
            end
          elsif lrs = arg[:locranges]
            lrs
          elsif tks = arg[:tokens]
            if tks.size == 2
              tks.collect { |tk| tks_to_location_range tk }
            else
              # 4 tokens
              # collect_slice would be nice ...
              tks.each_slice(2).to_a.collect do |tkpair|
                tks_to_location_range(*tkpair)
              end
            end
          else
            Log.info "wtf: #{arg}".on_red
            nil
          end
        end
      end
    end
    
    def self.included base
      base.extend ClassMethods
    end
  end

  class FDiffDelta
    include Loggable, FDiff

    attr_reader :first_location
    attr_reader :second_location
    attr_reader :diff_type
    attr_reader :message

    def initialize msg, *args
      @first_location, @second_location = self.class.convert_to_locations args
      @diff_type = diff_type
      @message = msg
    end

    def eql? other
      (self <=> other) == 0
    end

    def hash
      to_s.hash
    end

    # returns "1" (if same line) or "1,3" (multiple lines)
    def to_line_string lr
      str = lr.from.line.to_s.dup
      if lr.from.line != lr.to.line
        str << "," << lr.to.line.to_s
      end
      str
    end

    # returns 1a,8, 3,14c4,10 ...
    def to_diff_summary_string
      to_line_string(@first_location) << @diff_type << to_line_string(@second_location)
    end

    def to_s
      str = "["
      str << @diff_type.to_s
      str << " from: " << @first_location.to_s
      if @second_location
        str << " to: " << @second_location.to_s
      end
      str << "] (" << @message << ")"
    end

    def compare a, b
      a ? (b ? a <=> b : 1) : (b ? -1 : 0)
    end

    def <=> other
      cmp = compare first_location, other.first_location
      return cmp if cmp != 0

      cmp = compare second_location, other.second_location
      return cmp if cmp != 0
      
      cmp = compare diff_type, other.diff_type
      return cmp if cmp != 0
      
      compare message, other.message
    end
  end

  class FDiffAdd < FDiffDelta
    def diff_type
      "a"
    end

    def print_context dw, str
      dw.print_to str, self
    end

    def print_no_context dw, str
      dw.print_to str, self
    end
  end

  class FDiffChange < FDiffDelta
    def diff_type
      "c"
    end

    def print_context dw, str
      dw.print_from str, self
      str << DiffJ::IO::EOLN
      dw.print_to str, self
    end

    def print_no_context dw, str
      dw.print_from str, self
      str << "---"
      str << DiffJ::IO::EOLN
      dw.print_to str, self
    end
  end

  class FDiffDelete < FDiffDelta
    def diff_type
      "d"
    end

    def print_context dw, str
      dw.print_from str, self
    end

    def print_no_context dw, str
      dw.print_from str, self
    end
  end

  class FDiffCode < FDiffDelta
    def print_no_context dw, str
      dw.print_from str, self
      str << "---"
      str << DiffJ::IO::EOLN
      dw.print_to str, self
    end
  end

  class FDiffCodeAdded < FDiffCode
    def diff_type
      "a"
    end

    def print_context dw, str
      dw.print_to str, self
    end
  end

  class FDiffCodeDeleted < FDiffCode
    def diff_type
      "d"
    end

    def print_context dw, str
      dw.print_from str, self
    end
  end
end
