#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/util/diff/thresholds'

include Java

module DiffJ
  # returns longest common subsequences within two enumerables
  module DiffLCS
    class Delta
      include Comparable, Loggable
      
      attr_reader :delete_start
      attr_reader :delete_end
      attr_reader :add_start
      attr_reader :add_end
      
      def initialize delete_start, delete_end, add_start, add_end
        @delete_start = delete_start
        @delete_end = delete_end
        @add_start = add_start
        @add_end = add_end
      end

      def is_add?
        @delete_end.nil?
      end

      def is_delete?
        @add_end.nil?
      end

      def is_change?
        @add_end && @delete_end
      end

      def extend_added idx
        info "self: #{self.inspect}".blue.bold
        if @delete_end
          info "should be a change"
          Change.new @delete_start, @delete_end, @add_start, idx
        else
          info "is (still) an add"
          @add_end = idx
          self
        end
      end

      def extend_deleted idx
        info "self: #{self.inspect}".green.bold
        if @add_end
          info "should be a change"
          Change.new @delete_start, idx, @add_start, @add_end
        else
          info "is (still) a delete"
          @delete_end = idx
          self
        end
      end

      def _compare mine, theirs
        if mine.nil? || mine == -1
          theirs.nil? || theirs == -1 ? 0 : 1
        else
          mine <=> theirs
        end
      end

      def to_s
        "del: [#{@delete_start}, #{@delete_end}]; add: [#{@add_start}, #{@add_end}]"
      end

      def <=> other
        if other.java_kind_of? org.incava.ijdk.util.diff.Difference
          (_compare(@delete_start, other.getDeletedStart()).nonzero? ||
           _compare(@delete_end,   other.getDeletedEnd()).nonzero?   ||
           _compare(@add_start,    other.getAddedStart()).nonzero?   ||
           _compare(@add_end,      other.getAddedEnd()).nonzero?    ||
           0)
        else
          (_compare(@delete_start, other.delete_start).nonzero? ||
           _compare(@delete_end,   other.delete_end).nonzero?   ||
           _compare(@add_start,    other.add_start).nonzero?   ||
           _compare(@add_end,      other.add_end).nonzero?    ||
           0)
        end
      end
    end

    class Add < Delta
      attr_reader :add_end
      
      def initialize delete_start, delete_end, add_start, add_end
        super
      end
    end

    class Delete < Delta
      attr_reader :delete_end
      
      def initialize delete_start, delete_end, add_start, add_end
        super
      end
    end

    class Change < Delta
      attr_reader :add_end

    end
  end
end
