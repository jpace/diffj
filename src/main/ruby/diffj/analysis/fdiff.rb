#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/io/location'

include Java

java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

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
        LocationRange.new to_begin_location(from), to_end_location(to)
      end

      def locs_to_location_range from, to
        from && LocationRange.new(from, to)
      end

      # handles legacy overloading of FDiff* parameters for tokens, locations,
      # and location ranges.
      def convert_to_locations args
        if args.size == 1
          arg = args[0]
          if locs = arg[:locations]
            from_rg = locs_to_location_range(locs[0], locs[1])
            to_rg = locs_to_location_range(locs[2], locs[3])
            [ from_rg, to_rg ]
          elsif lrs = arg[:locranges]
            lrs
          elsif tks = arg[:tokens]
            if tks.size == 2
              from_rg = tks_to_location_range tks[0]
              to_rg = tks_to_location_range tks[1]
              [ from_rg, to_rg ]
            else
              # 4 tokens
              from_rg = tks_to_location_range(tks[0], tks[1])
              to_rg = tks_to_location_range(tks[2], tks[3])
              [ from_rg, to_rg ]
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

  class FDiffDelta < org.incava.analysis.FileDiff
    include Loggable, FDiff

    attr_reader :first_location
    attr_reader :second_location
    attr_reader :diff_type
    attr_reader :message

    def initialize msg, *args
      super diff_type, msg, *(@first_location, @second_location = self.class.convert_to_locations(args))
      # @first_location, @second_location = self.class.convert_to_locations(args)
      @diff_type = diff_type.to_s
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
      fromLine = lr.getStart().getLine();
      endLine = lr.getEnd().getLine();
      str = ""
      str << fromLine.to_s
      if fromLine != endLine
        str << "," << endLine.to_s
      end
      str
    end

    # returns 1a,8, 3,14c4,10 ...
    def to_diff_summary_string
      str = ""
      str << to_line_string(@first_location)
      str << @diff_type
      str << to_line_string(@second_location)
    end

    def to_s
      return "foo" if true
      
      str = ""
      str << "["
      str << @diff_type.to_s
      str << " from: " << (@first_location ? @first_location.to_s : "null")
      if @second_location
        str << " to: " << (@second_location ? @second_location.to_s : "null")
      end
      str << "] (" << @message << ")"
      str
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
    include Loggable, FDiff

    def diff_type
      org.incava.analysis.FileDiff::Type::ADDED
    end

    def print_context dw, sb
      dw.print_to sb, self
    end

    def print_no_context dw, sb
      dw.print_to sb, self
    end
  end

  class FDiffChange < FDiffDelta
    include Loggable, FDiff

    def diff_type
      org.incava.analysis.FileDiff::Type::CHANGED
    end

    def print_context dw, sb
      dw.print_from sb, self
      sb.append DiffJ::IO::EOLN
      dw.print_to sb, self
    end

    def print_no_context dw, sb
      dw.print_from sb, self
      sb.append "---"
      sb.append DiffJ::IO::EOLN
      dw.print_to sb, self
    end
  end

  class FDiffDelete < FDiffDelta
    include Loggable, FDiff

    def diff_type
      org.incava.analysis.FileDiff::Type::DELETED
    end

    def print_context dw, sb
      dw.print_from sb, self
    end

    def print_no_context dw, sb
      dw.print_from sb, self
    end
  end

  class FDiffCode < FDiffDelta
    include Loggable, FDiff

    def print_no_context dw, sb
      dw.print_from sb, self
      sb.append "---"
      sb.append DiffJ::IO::EOLN
      dw.print_to sb, self
    end
  end

  class FDiffCodeAdded < FDiffCode
    include Loggable, FDiff

    def diff_type
      org.incava.analysis.FileDiff::Type::ADDED
    end

    def print_context dw, sb
      dw.print_to sb, self
    end
  end

  class FDiffCodeDeleted < FDiffCode
    include Loggable, FDiff

    def diff_type
      org.incava.analysis.FileDiff::Type::DELETED
    end

    def print_context dw, sb
      dw.print_from sb, self
    end
  end
end
