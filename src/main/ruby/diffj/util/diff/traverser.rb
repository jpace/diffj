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

        info "matches: #{@matches}".bold.red.on_green
        info "matches.size: #{@matches.size}".bold.red.on_green

        @a_size = a_size
        @b_size = b_size

        info "a_size: #{@a_size}; b_size: #{@b_size}".on_blue
        
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

        info "aidx: #{@aidx}; bidx: #{@bidx}".on_blue
        info "lastmatch: #{lastmatch}".on_blue

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
          info "@aidx: #{@aidx}; @bidx: #{@bidx}".bold
          if @aidx == @a_size   # last A
            info "@aidx: #{@aidx}; @bidx: #{@bidx}".red
            elements_added @b_size - 1
            return
          elsif @bidx == @b_size # last B?
            info "@aidx: #{@aidx}; @bidx: #{@bidx}".yellow
            elements_deleted @a_size - 1
            return
          end

          if @aidx < @a_size
            info "@aidx: #{@aidx}; @bidx: #{@bidx}".magenta
            element_deleted
          end

          if @bidx < @b_size
            info "@aidx: #{@aidx}; @bidx: #{@bidx}".cyan
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

        info "pending: #{@pending}".bold.yellow

        @aidx = aend + 1
      end

      # Invoked for elements in <code>b</code> and not in <code>a</code>.
      def elements_added bend
        if @pending.nil?
          @pending = Add.new @aidx, @bidx, bend
        else
          @pending = @pending.extend_added bend
        end

        info "pending: #{@pending}".bold.green

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

        info "pending: #{@pending}".bold.magenta

        @aidx += 1
        @bidx += 1
      end
    end
  end
end
