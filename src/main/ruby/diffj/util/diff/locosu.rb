#!/usr/bin/jruby -w
# -*- ruby -*-

require 'riel/log'
require 'java'
require 'diffj/util/diff/delta'
require 'diffj/util/diff/lcs'

include Java

module DiffJ
  class OrigLocosu < org.incava.ijdk.util.diff.Diff
    def initialize from, to
      super from, to
    end
  end

  # returns longest common subsequences within two enumerables
  class Locosu < org.incava.ijdk.util.diff.Diff
    def initialize from, to
      super from, to
      @from = from
      @to = to
    end

    def diff
      locosu_traverse_sequences
      addPending
      getDiffs()
    end

    def locosu_traverse_sequences
      matches = getLongestCommonSubsequences()
      
      lastA = @from.size() - 1
      lastB = @to.size() - 1
      bi = 0
      ai = 0

      lastMatch = matches.length - 1
      
      while ai <= lastMatch
        bLine = matches[ai]

        if bLine.nil?
          onANotB(ai, bi)
        else
          while bi < bLine
            onBNotA(ai, bi)
            bi += 1
          end
          onMatch(ai, bi)
          bi += 1
        end
        
        ai += 1
      end

      while ai <= lastA || bi <= lastB
        # last A?
        if ai == lastA + 1 && bi <= lastB
          while bi <= lastB
            onBNotA(ai, bi)
            bi += 1
          end
        end

        # last B?
        if bi == lastB + 1 && ai <= lastA
          while ai <= lastA
            onANotB(ai, bi);
            ai += 1
          end
        end

        if ai <= lastA
          onANotB(ai, bi)
          ai += 1
        end

        if bi <= lastB
          onBNotA(ai, bi)
          bi += 1
        end
      end
    end
  end
end
