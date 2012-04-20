#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  # returns longest common subsequences within two enumerables
  module DiffLCS
    attr_reader :delete_start
    attr_reader :delete_end
    attr_reader :add_start
    attr_reader :add_end

    class Delta
      def initialize delete_start, delete_end, add_start, add_end
        @delete_start = delete_start
        @delete_end = delete_end
        @add_start = add_start
        @add_end = add_end
      end
    end

    # The links as used in the Diff/LCS code.
    class LCSTable 
      include Loggable
      
      def initialize 
        @links = Hash.new
      end

      # Updates the value for the key <code>k</code>.
      def update i, j, k
        info "i: #{i}"
        info "j: #{j}"
        info "k: #{k}"

        value = k > 0 ? @links[k - 1] : nil
        @links[k] = [ value, i, j ]
      end

      # Returns the links starting from <code>key</code>.
      def get_chain key
        info "key: #{key}"

        chain = Hash.new
        link = @links[key]
        while link
          x = link[1]
          y = link[2];
          chain[x] = y
          link = link[0]
        end
        chain
      end
    end

    class Comparator < org.incava.ijdk.util.diff.Diff
      include Loggable

      def initialize from, to
        super from, to
        
        @from = from
        @to = to
        @diffs = Array.new
        @pending = nil
        @thresholds = Hash.new
        @comparator = nil       # java.util.Comparator
      end

      def lcs
        comp_get_longest_common_subsequences
      end

      def are_equal? x, y
        # comparator == null ? x.equals(y) : comparator.compare(x, y) == 0
        x == y
      end
      
      def comp_get_longest_common_subsequences
        a = @from
        b = @to

        aStart = 0
        aEnd = a.size() - 1

        bStart = 0
        bEnd = b.size() - 1

        matches = java.util.TreeMap.new
        
        while aStart <= aEnd && bStart <= bEnd && are_equal?(a[aStart], b[bStart])
          matches.put aStart, bStart
          aStart += 1
          bStart += 1
        end

        while aStart <= aEnd && bStart <= bEnd && are_equal?(a[aEnd], b[bEnd])
          matches.put aEnd, bEnd
          aEnd -= 1
          bEnd -= 1
        end

        # addMatches matches, aStart, aEnd, bStart, bEnd
        comp_add_matches matches, aStart, aEnd, bStart, bEnd

        to_array matches
      end
      
      def comp_get_b_matches bStart, bEnd
        bMatches = nil

        if @comparator
          # we don't really want them sorted, but this is the only Map
          # implementation (as of JDK 1.4) that takes a comparator.
          bMatches = java.util.TreeMap.new(comparator)
          info "bMatches".on_red
        elsif @from.size() > 0 && @from[0].java_kind_of?(java.lang.Comparable)
          # this uses the Comparable interface
          bMatches = java.util.TreeMap.new
          info "bMatches".on_red
        else
          # this just uses hashCode()
          bMatches = java.util.HashMap.new
          info "bMatches".on_red
        end

        (bStart .. bEnd).each do |bi|
          info "bi: #{bi.class}"
          
          key = @to[bi]
          positions = bMatches.get(key)
          if positions.nil?
            positions = java.util.ArrayList.new
            bMatches.put(key, positions)
          end

          positions.add bi
        end

        bMatches
      end

      def comp_add_matches matches, aStart, aEnd, bStart, bEnd
        bMatches = comp_get_b_matches bStart, bEnd
        info "bMatches: #{bMatches}".green

        links = LCSTable.new

        thresh = org.incava.ijdk.util.diff::Thresholds.new()

        (aStart .. aEnd).each do |i|
          info "i: #{i}".on_red
          aElement = @from[i]
          positions = bMatches.get(aElement)

          if positions
            k = 0
            pit = positions.listIterator(positions.size())
            while pit.hasPrevious()
              j = pit.previous();
              k = thresh.insert(j, k);
              if k
                links.update(i, j, k);
              end
            end
          end
        end

        info "matches: #{matches}".yellow

        if !thresh.isEmpty()
          ti = thresh.lastKey();
          chain = links.get_chain ti
          info "chain: #{chain}".yellow
          chain.each do |key, value|
            info "key: #{key}".yellow
            info "value: #{value}".yellow
            matches.put(key, value)
          end
          info "matches: #{matches}".cyan
        end
      end
      
      def to_array map
        size = map.isEmpty() ? 0 : 1 + map.lastKey()
        ary = Array.new
        map.keySet().each do |key|
          ary[key] = map.get(key)
        end
        ary
      end
 
      def compare from, to
      end
    end
  end
end
