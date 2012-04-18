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

class DiffJ::LocationRangeTestCase < DiffJ::TestCase
  include Loggable

  def setup
    loc00 = loc 0, 0
    loc12 = loc 1, 2
    loc34 = loc 3, 4
    loc56 = loc 5, 6

    @lrg1234 = locrg loc12, loc34
    @lrg1256 = locrg loc12, loc56
    @lrg0034 = locrg loc00, loc34

    super
  end
  
  def test_ctor
    lr = locrg loc(17, 3), loc(6, 28)
    assert_equal loc(17, 3), lr.from
    assert_equal loc(6, 28), lr.to
  end

  def test_to_s
    loc = locrg loc(8, 66), loc(1, 123)
    assert_equal "[8:66 .. 1:123]", loc.to_s
  end

  def test_equals
    loc12 = loc(1, 2)
    loc34 = loc(3, 4)
    loc56 = loc(5, 6)

    assert @lrg1234 == @lrg1234
    assert @lrg1234 != @lrg1256
    assert @lrg1234 != @lrg0034
  end

  def assert_spaceship exp, from, to
    assert_equal exp, from <=> to
  end
    
  def test_spaceship
    loc00 = loc(0, 0)
    loc12 = loc(1, 2)
    loc34 = loc(3, 4)
    loc56 = loc(5, 6)

    assert_spaceship( 0, @lrg1234, @lrg1234)
    assert_spaceship(-1, @lrg1234, @lrg1256)
    assert_spaceship( 1, @lrg1234, @lrg0034)
  end
end
