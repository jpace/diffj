#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  class FDiffChange < org.incava.analysis.FileDiffChange
    include Loggable

    class << self
      def to_begin_location tk
        tk && org.incava.ijdk.text.Location.new(tk.beginLine, tk.beginColumn)
      end
    
      def to_end_location tk
        tk && org.incava.ijdk.text.Location.new(tk.endLine, tk.endColumn)
      end
      
      alias_method :old_new, :new
      
      def new *args
        Log.info "args: #{args}".cyan
        
        # convert to locations ...
        msg = args.shift
        locs = Array.new
        if args.size == 2
          # two tokens, first 
          locs << to_begin_location(args[0])
          locs << to_end_location(args[0])
          locs << to_begin_location(args[1])
          locs << to_end_location(args[1])
        else
          locs << to_begin_location(args[0])
          locs << to_end_location(args[1])
          locs << to_begin_location(args[2])
          locs << to_end_location(args[3])
        end
        Log.info "msg: #{msg}"
        Log.info "locs: #{locs}"
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
