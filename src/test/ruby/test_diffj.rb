#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'

import org.incava.diffj.DiffJ

class DiffJTest < Test::Unit::TestCase
  def test_diffj_ctor
    diffj = DiffJ.new %w{ /tmp/CU.0.java /tmp/CU.1.java }, false, false, false
    assert_not_nil diffj
  end
end
