#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'

class DiffJTest < Test::Unit::TestCase
  def test_diffj_ctor
    fnames = %w{ /tmp/CU.0.java /tmp/CU.1.java }
    brief = false
    context = true
    highlight = true
    recurse = true
    fromname = nil
    fromver = "1.5"
    toname = nil
    tover = "1.5"
    
    diffj = DiffJRuby.new brief, context, highlight, recurse, fromname, fromver, toname, tover
    diffj.process_things fnames
    
    assert_not_nil diffj
  end
end
