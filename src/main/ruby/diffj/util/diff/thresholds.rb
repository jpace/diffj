#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel/log'

module DiffJ
  module DiffLCS
    class Thresholds < Hash
      include Loggable
      
      def initialize
        @last_key = nil         # highest in sort order, so we don't keep resorting the keys
      end

      # returns whether the value for the given index is greater than val.
      def greater_than? idx, val
        curr = self[idx]
        curr && val && curr > val
      end

      # returns whether the value for the given index is less than val.
      def less_than? idx, val
        # we're using nil instead of having the elements in the array
        # initialized to zero.
        curr = self[idx]
        curr && (val.nil? || curr < val)
      end

      # Adds the given value to the "end" of the threshold map, that is, with
      # the greatest index/key.
      def append value
        add_idx = empty? ? 0 : last_key + 1
        self[add_idx] = value
      end
      
      def last_key
        @last_key
      end

      def last_value
        self[last_key]
      end

      def []= key, value
        if @last_key.nil? || key > @last_key
          @last_key = key
        end
        super
      end

      # Inserts the given values into the threshold map.
      def insert j, k
        if k && k != 0 && greater_than?(k, j) && less_than?(k - 1, j)
          self[k] = j
          return k
        end
        
        hi = -1
            
        if k && k != 0
          hi = k
        elsif !empty?
          hi = last_key
        end

        # off the end?
        if hi == -1 || j > last_value
          append j
          return hi + 1
        end
                
        # binary search for insertion point:
        lo = 0

        while lo <= hi
          index = (hi + lo) / 2
          val   = self[index]
          cmp   = j <=> val

          if cmp == 0
            return nil
          elsif cmp > 0
            lo = index + 1
          else
            hi = index - 1
          end
        end

        self[lo] = j
        lo
      end
    end
  end
end
