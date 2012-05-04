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
    # The links as used in the Diff/LCS code.
    class LCSTable 
      include Loggable
      
      def initialize 
        @links = Hash.new
      end

      # Updates the value for the key <code>k</code>.
      def update i, j, k
        value = k > 0 ? @links[k - 1] : nil
        @links[k] = [ value, i, j ]
      end

      # Returns the links starting from <code>key</code>.
      def get_chain key
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

    class LCS
      include Loggable

      def initialize from, to
        @from = from
        @to = to
        compute
      end

      def matches
        @matches
      end

      def skip_common_from_start 
        while @from_start <= @from_end && @to_start <= @to_end && @from[@from_start] == @to[@to_start]
          @match_map[@from_start] = @to_start
          @from_start += 1
          @to_start += 1
        end
      end

      def skip_common_from_end
        while @from_start <= @from_end && @to_start <= @to_end && @from[@from_end] == @to[@to_end]
          @match_map[@from_end] = @to_end
          @from_end -= 1
          @to_end -= 1
        end
      end

      def compute
        @from_start = 0
        @from_end = @from.size - 1

        @to_start = 0
        @to_end = @to.size - 1

        @match_map = Hash.new

        skip_common_from_start
        skip_common_from_end

        add_matches

        info "@match_map: #{@match_map}"
        @match_map.each do |key, value|
          info "@match_map[#{key}]: #{value}"
        end

        @matches = Array.new(@match_map.empty? ? 0 : 1 + @match_map.keys[-1])
        @match_map.each do |key, value|
          @matches[key] = value
        end
      end
      
      def get_to_matches
        to_matches = Hash.new { |h, k| h[k] = Array.new }

        (@to_start .. @to_end).each do |bi|
          info "bi: #{bi}"
          to = @to[bi]
          info "to: #{to}"
          currval = to_matches[to]
          info "currval: #{currval}"

          to_matches[to] << bi
        end

        to_matches.each do |key, value|
          info "#{key} (#{key.hash}) => #{value}"
        end

        to_matches
      end

      def add_matches
        to_matches = get_to_matches
        info "to_matches: #{to_matches.inspect}"
        to_matches.each do |key, value|
          info "to_matches[#{key} (#{key.hash})]: #{value}"
        end

        links = LCSTable.new
        thresholds = Thresholds.new

        (@from_start .. @from_end).each do |i|
          from_element = @from[i]
          # info "from_element: #{from_element}"
          next unless positions = to_matches[from_element]

          k = 0
          (positions.size - 1).downto(0) do |pidx|
            j = positions[pidx]
            k = thresholds.insert j, k
            if k
              links.update i, j, k
            end
          end
        end

        if !thresholds.empty?
          ti = thresholds.last_key
          chain = links.get_chain ti
          info "chain: #{chain}"
          chain.each do |key, value|
            @match_map[key] = value
          end
        end
      end
    end
  end
end
