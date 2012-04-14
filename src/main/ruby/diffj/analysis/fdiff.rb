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
        DiffJ::IO::Location.beginning tk
      end
    
      def to_end_location tk
        DiffJ::IO::Location.ending tk
      end      

      # handles legacy overloading of FDiff* parameters for tokens, locations,
      # and location ranges.
      def convert_to_locations args
        if args.size == 1
          arg = args[0]
          if locs = arg[:locations]
            locs
          elsif lrs = arg[:locranges]
            [ lrs[0].getStart(), lrs[0].getEnd(), lrs[1].getStart(), lrs[1].getEnd() ]
          elsif tks = arg[:tokens]
            if tks.size == 2
              locs = Array.new
              locs << to_begin_location(tks[0])
              locs << to_end_location(tks[0])

              locs << to_begin_location(tks[1])
              locs << to_end_location(tks[1])
              locs
            else
              # 4 tokens
              locs = Array.new
              locs << to_begin_location(tks[0])
              locs << to_end_location(tks[1])
              
              locs << to_begin_location(tks[2])
              locs << to_end_location(tks[3])
              locs
            end
          else
            Log.info "wtf: #{arg}".on_red
            nil
          end
        end
      end

      def create ctor, args
        msg = args.shift
        locs = convert_to_locations args
        Log.info "msg: #{msg}".on_red
        Log.info "locs: #{locs.inspect}".on_red
        send ctor, msg, *locs
      end
    end

    def self.included base
      base.extend ClassMethods
    end
  end

  class FDiffAdd < org.incava.analysis.FileDiffAdd
    include Loggable, FDiff

    class << self
      alias_method :old_new, :new
      def new *args
        create :old_new, args
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

    class << self
      alias_method :old_new, :new
      def new *args
        create :old_new, args
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

    class << self
      alias_method :old_new, :new
      def new *args
        create :old_new, args
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

  class FDiffCodeAdded < org.incava.analysis.FileDiffAdd
    include Loggable, FDiff

    class << self
      alias_method :old_new, :new
      def new *args
        create :old_new, args
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

  class FDiffCodeDeleted < org.incava.analysis.FileDiffDelete
    include Loggable, FDiff

    class << self
      alias_method :old_new, :new
      def new *args
        create :old_new, args
      end
    end
  end
end
