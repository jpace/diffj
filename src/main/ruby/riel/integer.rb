#!/usr/bin/ruby -w
# -*- ruby -*-

class Integer
  POS_NEG_NUMERIC_RE = Regexp.new('^([\-\+])?(\d+)$')
  
  class << self
    # returns the value as an integer, if it is not negative
    def negative? val
      negnumre = Regexp.new '^(\-\d+)$'
      
      if val.nil?
        nil
      elsif val.kind_of? Integer
        val < 0 && val
      elsif md = negnumre.match(val.to_s)
        md[1].to_i
      else
        nil
      end
    end
  end
end
