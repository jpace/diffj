#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/tc'

include Java
include DiffJ::IO

class DiffJ::LocationTestCase < DiffJ::TestCase
  include Loggable

  def test_ctor
    lc = loc 17, 3
    assert_equal 17, lc.line
    assert_equal 3, lc.column
  end

  def test_to_s
    lc = loc 632, 5
    assert_equal "632:5", lc.to_s
  end

  def test_equals
    assert loc(111, 222) == loc(111, 222)
    assert loc(111, 222) != loc(111, 223)
    assert loc(112, 222) != loc(111, 222)
  end

  def assert_spaceship exp, aline, acol, bline, bcol
    assert_equal exp, loc(aline, acol) <=> loc(bline, bcol)
  end
    
  def test_spaceship
    assert_spaceship( 0, 10, 14, 10, 14)
    assert_spaceship( 1,  5, 10,  5,  9)
    assert_spaceship(-1,  5, 18,  6, 18)
  end
end
