#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/util/diff/delta'
require 'diffj/util/diff/lcs'

include Java

module DiffJ
  # returns longest common subsequences within two enumerables
  module DiffLCS
    # DiffDelta = org.incava.ijdk.util.diff.Difference
    LCSDelta = DiffJ::DiffLCS::Delta

    class OrigDelta < DiffJ::DiffLCS::Delta
      def extend_added idx
        @add_end = idx
        self
      end

      def extend_deleted idx
        @delete_end = idx
        self
      end
    end

    # works most similar to the old Java one
    class OrigTraverser
      include Loggable
      
      def initialize matches, a_size, b_size
        @diffs = Array.new
        @pending = nil
        compute matches, a_size, b_size
      end

      def diffs
        @diffs
      end

      def compute matches, a_size, b_size
        traverse_sequences matches, a_size, b_size

        # add the last difference, if pending:
        if @pending
          @diffs << @pending
        end
      end

      def traverse_sequences matches, a_size, b_size
        bi = 0
        ai = 0

        lastmatch = matches.length - 1

        while ai <= lastmatch
          bline = matches[ai]

          if bline.nil?
            on_a_not_b ai, bi
          else
            while bi < bline
              on_b_not_a ai, bi
              bi += 1
            end              

            on_match ai, bi
            bi += 1
          end
          ai += 1
        end

        while ai < a_size || bi < b_size
          # last A?
          if ai == a_size && bi < b_size
            while bi < b_size
              on_b_not_a ai, bi
              bi += 1
            end
          end

          # last B?
          if bi == b_size && ai < a_size
            while ai < a_size
              on_a_not_b ai, bi
              ai += 1
            end
          end

          if ai < a_size
            on_a_not_b ai, bi
            ai += 1
          end

          if bi < b_size
            on_b_not_a ai, bi
            bi += 1
          end
        end
      end

      # Invoked for elements in <code>a</code> and not in <code>b</code>.
      def on_a_not_b ai, bi
        if @pending.nil?
          @pending = OrigDelta.new ai, ai, bi, nil
        else
          @pending = @pending.extend_deleted ai
        end
      end

      # Invoked for elements in <code>b</code> and not in <code>a</code>.
      def on_b_not_a ai, bi
        if @pending.nil?
          @pending = OrigDelta.new ai, nil, bi, bi
        else
          @pending = @pending.extend_added bi
        end
      end

      # Invoked for elements matching in <code>a</code> and <code>b</code>.
      def on_match ai, bi
        if @pending
          @diffs << @pending
          @pending = nil
        end
      end
    end
  end
end
