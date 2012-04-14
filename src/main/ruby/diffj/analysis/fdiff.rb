#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/io/location'

include Java

module DiffJ
  module FDiff
    module ClassMethods
      def to_begin_location tk
        tk && DiffJ::IO::Location.new(tk.beginLine, tk.beginColumn)
      end
    
      def to_end_location tk
        tk && DiffJ::IO::Location.new(tk.endLine, tk.endColumn)
      end      

      # handles legacy overloading of FDiff* parameters for tokens, locations,
      # and location ranges.
      def convert_to_locations args
        Log.info "convert_to_locations:::::::::::::::::::::::::::::::::::::::::::::::::::::::".on_magenta
        if args.size == 1
          Log.info "args[0]: #{args[0]}"
          arg = args[0]
          if locs = arg[:locations]
            locs
          elsif lrs = arg[:locranges]
            [ lrs[0].getStart(), lrs[0].getEnd(), lrs[1].getStart(), lrs[1].getEnd() ]
          else
            Log.info "wtf: #{arg}".on_red
            nil
          end
        elsif args.size == 2
          locs = Array.new
          locs << to_begin_location(args[0])
          locs << to_end_location(args[0])
          locs << to_begin_location(args[1])
          locs << to_end_location(args[1])
          locs
        else
          # 4 tokens
          locs = Array.new
          locs << to_begin_location(args[0])
          locs << to_end_location(args[1])
          locs << to_begin_location(args[2])
          locs << to_end_location(args[3])
          locs
        end
      end
    end
  end

  class FDiffAdd < org.incava.analysis.FileDiffAdd
    include Loggable, FDiff
    extend FDiff::ClassMethods

    class << self
      alias_method :old_new, :new
      
      def new *args
        msg = args.shift
        locs = convert_to_locations args
        old_new msg, *locs
      end
    end

    def initialize msg, from_loc_start, from_loc_end, to_loc_start, to_loc_end
      info "msg: #{msg}".cyan
      info "from_loc_start: #{from_loc_start}".cyan
      info "from_loc_end: #{from_loc_end}".cyan
      info "to_loc_start: #{to_loc_start}".cyan
      info "to_loc_end: #{to_loc_end}".cyan
      super
    end
  end

  class FDiffChange < org.incava.analysis.FileDiffChange
    include Loggable, FDiff
    extend FDiff::ClassMethods

    class << self
      alias_method :old_new, :new
      
      def new *args
        msg = args.shift
        locs = convert_to_locations args
        old_new msg, *locs
      end
    end

    def initialize msg, from_loc_start, from_loc_end, to_loc_start, to_loc_end
      info "msg: #{msg}".cyan
      info "from_loc_start: #{from_loc_start}".cyan
      info "from_loc_end: #{from_loc_end}".cyan
      info "to_loc_start: #{to_loc_start}".cyan
      info "to_loc_end: #{to_loc_end}".cyan
      super
    end
  end

  class FDiffDelete < org.incava.analysis.FileDiffDelete
    include Loggable, FDiff
    extend FDiff::ClassMethods

    class << self
      alias_method :old_new, :new
      
      def new *args
        msg = args.shift
        locs = convert_to_locations args
        old_new msg, *locs
      end
    end

    def initialize msg, from_loc_start, from_loc_end, to_loc_start, to_loc_end
      info "msg: #{msg}".cyan
      info "from_loc_start: #{from_loc_start}".cyan
      info "from_loc_end: #{from_loc_end}".cyan
      info "to_loc_start: #{to_loc_start}".cyan
      info "to_loc_end: #{to_loc_end}".cyan
      super
    end
  end
end
