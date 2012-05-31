#!/usr/bin/ruby -w
# -*- ruby -*-

class Hash

  $-w = false
  def to_s
    "{ " + collect { |k, v| k.to_s + " => " + v.to_s }.join(", ") + " }"
  end
  $-w = true

  #$$$ add HashToArray
  # checkins_by_month = Hash.new { |hash, key| hash[key] = Array.new }

end
