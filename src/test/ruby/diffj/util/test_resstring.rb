#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'
require 'diffj/util/resstring'

include Java
include DiffJ

class DiffJ::ResourceStringTestCase < DiffJ::TestCase
  include Loggable

  def test_is_string
    rs = ResourceString.new "hello"
    assert_kind_of ResourceString, rs
    assert_kind_of String, rs
  end

  def test_format_one
    rs = ResourceString.new "{0}"
    assert_equal "hello", rs.format("hello")
  end

  def test_format_two
    rs = ResourceString.new "this is {0}; this is {1}"
    assert_equal "this is something; this is nothing", rs.format("something", "nothing")
  end
end
