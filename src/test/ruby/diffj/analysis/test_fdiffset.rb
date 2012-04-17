#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/analysis/fdiff'

include Java

java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

class DiffJ::FDiffSetTestCase < Test::Unit::TestCase
  include Loggable

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def setup
    @fdc_0 = DiffJ::FDiffChange.new "variable changed from fl to flotilla", :locranges => [ loc_rg(1, 2, 3, 4), loc_rg(5, 6, 7, 8) ]
    @fdc_1 = DiffJ::FDiffChange.new "variable changed from fl to flotilla", :locranges => [ loc_rg(1, 2, 3, 4), loc_rg(5, 6, 7, 8) ]
    @fdc_2 = DiffJ::FDiffChange.new "type changed from interface to class", :locranges => [ loc_rg(11, 12, 13, 14), loc_rg(21, 20, 19, 18) ]

    info "self: #{self}".yellow
  end

  # $$$ @todo: move this to test_fdiff.rb
  def test_fdiffs
    assert_equal @fdc_0.hash, @fdc_1.hash
    assert @fdc_0.eql? @fdc_1
    assert_equal 0, @fdc_0 <=> @fdc_1
  end

  def test_add_and_size
    fds = DiffJ::FDiff::FDiffSet.new
    assert_equal 0, fds.size
    assert fds.empty?

    fds << @fdc_0
    assert_equal 1, fds.size
    
    fds << @fdc_1
    assert_equal 1, fds.size

    fds << @fdc_2
    assert_equal 2, fds.size
  end

  def test_was_added_on_init
    fds = DiffJ::FDiff::FDiffSet.new
    assert !fds.was_added?
  end

  def test_was_added_on_add
    fds = DiffJ::FDiff::FDiffSet.new

    fds << @fdc_0
    assert fds.was_added?

    fds << @fdc_1
    assert fds.was_added?
    
    fds << @fdc_2
    assert fds.was_added?
  end

  def test_was_added_after_clear
    fds = DiffJ::FDiff::FDiffSet.new

    fds << @fdc_0
    assert fds.was_added?

    fds.clear

    assert fds.empty?
    assert fds.was_added?
  end
end
