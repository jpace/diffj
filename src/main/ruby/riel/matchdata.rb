#!/usr/bin/ruby -w
# -*- ruby -*-

class MatchData

  origw = $-w
  $-w = false
  def inspect
    to_a.inspect
  end
  $-w = origw

end
