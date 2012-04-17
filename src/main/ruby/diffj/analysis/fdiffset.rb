#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'set'

include Java

module DiffJ
  module FDiff
    class FDiffSet < SortedSet
      # class FDiffSet < java.util.TreeSet
      include Loggable
      
      def initialize
        super
        @added = false
      end

      def add fd
        @added = true
        super
      end

      def << fd
        info "fd: #{fd}".green
        @added = true
        super
      end

      def was_added?
        @added
      end
    end
  end
end
