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
    DiffDelta = org.incava.ijdk.util.diff.Difference
    LCSDelta = DiffJ::DiffLCS::Delta

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

        info "lastmatch: #{lastmatch}".bold.blue
        
        while ai <= lastmatch
          bline = matches[ai]
          info "bline: #{bline}".bold

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

        info "ai: #{ai}; a_size: #{a_size}"
        info "bi: #{bi}; b_size: #{b_size}"

        while ai < a_size || bi < b_size
          info "ai: #{ai}; a_size: #{a_size}".yellow

          # last A?
          if ai == a_size && bi < b_size
            info "bi: #{bi}; b_size: #{b_size}".yellow.bold
            while bi < b_size
              on_b_not_a ai, bi
              bi += 1
            end
          end

          # last B?
          if bi == b_size && ai < a_size
            info "ai: #{ai}; a_size: #{a_size}".cyan.bold
            while ai < a_size
              on_a_not_b ai, bi
              ai += 1
            end
          end

          if ai < a_size
            info "ai: #{ai}; a_size: #{a_size}".blue.bold
            on_a_not_b ai, bi
            ai += 1
          end

          if bi < b_size
            info "bi: #{bi}; b_size: #{b_size}".magenta.bold
            on_b_not_a ai, bi
            bi += 1
          end
        end
      end

      # Invoked for elements in <code>a</code> and not in <code>b</code>.
      def on_a_not_b ai, bi
        if @pending.nil?
          @pending = LCSDelta.new ai, ai, bi, nil
        else
          @pending = @pending.extend_deleted ai
        end
      end

      # Invoked for elements in <code>b</code> and not in <code>a</code>.
      def on_b_not_a ai, bi
        if @pending.nil?
          @pending = LCSDelta.new ai, nil, bi, bi
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
