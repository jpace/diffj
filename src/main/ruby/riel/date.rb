#!/usr/bin/ruby -w
# -*- ruby -*-
#!ruby -w

require 'date'


class Date

  # Returns the number of days in the given month.
  
  def self.days_in_month(year, month)
    (Date.new(year, 12, 31) << (12 - month)).day
  end

end
