#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/diffjtestcase'
require 'diffj/io/location'

include Java
include DiffJ::IO

class DiffJ::LocationTestCase < DiffJ::TestCase
  include Loggable
  
  def test_ctor
    loc = Location.new 17, 3
    assert_equal 17, loc.line
    assert_equal 3, loc.column
  end

  def test_to_string
    loc = Location.new 632, 5
    assert_equal "632:5", loc.to_string
  end

  def test_equals
    assert Location.new(111, 222).equals(Location.new(111, 222))
    assert !Location.new(111, 222).equals(Location.new(111, 223))
    assert !Location.new(112, 222).equals(Location.new(111, 222))
  end

  def assert_compare_to exp, aline, acol, bline, bcol
    assert_equal exp, Location.new(aline, acol).compare_to(Location.new(bline, bcol))
  end
    
  def test_compare_to
    assert_compare_to( 0, 10, 14, 10, 14)
    assert_compare_to( 1,  5, 10,  5,  9)
    assert_compare_to(-1,  5, 18,  6, 18)
  end
end
