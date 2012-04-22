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
    class Traverser
      include Loggable
      
      def initialize matches, a_size, b_size
        @diffs = Array.new
        @pending = nil
        @matches = matches
        @a_size = a_size
        @b_size = b_size
        
        traverse_sequences

        if @pending
          @diffs << @pending
        end
      end

      def diffs
        @diffs
      end

      def traverse_sequences
        @bidx = 0
        @aidx = 0

        lastmatch = @matches.length - 1

        while @aidx <= lastmatch
          bline = @matches[@aidx]
          if bline.nil?
            element_deleted
          else
            if @bidx < bline
              elements_added bline - 1
            end
            elements_match
          end
        end

        while @aidx < @a_size || @bidx < @b_size
          if @aidx == @a_size   # last A
            elements_added @b_size - 1
            return
          elsif @bidx == @b_size # last B?
            elements_deleted @a_size - 1
            return
          end

          if @aidx < @a_size
            element_deleted
          end

          if @bidx < @b_size
            element_added
          end
        end
      end

      # Invoked for an element in <code>a</code> and not in <code>b</code>.
      def element_deleted
        elements_deleted @aidx
      end

      # Invoked for elements in <code>a</code> and not in <code>b</code>.
      def elements_deleted aend
        if @pending.nil?
          @pending = Delete.new @aidx, aend, @bidx
        else
          @pending = @pending.extend_deleted aend
        end
        @aidx = aend + 1
      end

      # Invoked for elements in <code>b</code> and not in <code>a</code>.
      def elements_added bend
        if @pending.nil?
          @pending = Add.new @aidx, @bidx, bend
        else
          @pending = @pending.extend_added bend
        end
        @bidx = bend + 1
      end

      # Invoked for an element in <code>b</code> and not in <code>a</code>.
      def element_added
        elements_added @bidx
      end

      # Invoked for elements matching in <code>a</code> and <code>b</code>.
      def elements_match
        if @pending
          @diffs << @pending
          @pending = nil
        end
        @aidx += 1
        @bidx += 1
      end
    end
  end
end
