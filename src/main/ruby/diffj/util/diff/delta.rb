#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/util/diff/thresholds'

module DiffJ
  # returns longest common subsequences within two enumerables
  module DiffLCS
    class Delta
      include Comparable
      
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

      def inspect
        to_s
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
        if @delete_end
          Change.new @delete_start, @delete_end, @add_start, idx
        else
          @add_end = idx
          self
        end
      end

      def extend_deleted idx
        if @add_end
          Change.new @delete_start, idx, @add_start, @add_end
        else
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

      def == other
        (self <=> other) == 0
      end

      def is_java_diff? diff
        defined?(Java::org.incava.ijdk.util.diff::Difference) && 
          diff.java_kind_of?(org.incava.ijdk.util.diff.Difference)
      end

      def <=> other
        if is_java_diff? other
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
      
      def initialize delete_start, add_start, add_end
        super delete_start, nil, add_start, add_end
      end
    end

    class Delete < Delta
      attr_reader :delete_end
      
      def initialize delete_start, delete_end, add_start
        super delete_start, delete_end, add_start, nil
      end
    end

    class Change < Delta
      attr_reader :add_end

      def extend_added idx
        @add_end = idx
        self
      end

      def extend_deleted idx
        @delete_end = idx
        self
      end
    end
  end
end
