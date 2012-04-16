#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/io/diffwriter/writer'
require 'diffj/io/diffwriter/no_context'

include Java

java_import org.incava.analysis.DetailedReport
java_import org.incava.analysis.FileDiffChange
java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

class DiffJ::WriterTestCase < Test::Unit::TestCase
  include Loggable

  FROMCONT  = Array.new
  #           0         1         2         3         4         5
  #            12345678901234567890123456789012345678901234567890
  FROMCONT << "And pleasant was his absolution."
  # FROMCONT << "He was an easy man to give penance,"  delete
  FROMCONT << "There as he wist to have a good pittance:" # change
  FROMCONT << "For unto a poor order for to give"
  FROMCONT << "Is signe that a man is well y-shrive."
  FROMCONT << "For if he gave, he durste make avant," # change
  FROMCONT << "He wiste that the man was repentant."  # change

  TOCONT    = Array.new
  #           0         1         2         3         4         5
  #            12345678901234567890123456789012345678901234567890
  TOCONT   << "And pleasant was his absolution."
  TOCONT   << "He was an easy man to give penance,"
  TOCONT   << "Where he know he would get good payment"
  TOCONT   << "For unto a poor order for to give"
  TOCONT   << "Is signe that a man is well y-shrive."
  TOCONT   << "For if he gave, he dared to boast,"       # change
  TOCONT   << "He knew that the man was repentant."      # change
  TOCONT   << "For many a man so hard is of his heart,"  # add
  TOCONT   << "He may not weep although him sore smart." # add

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def create_exp_str lines, ch
    lines.collect { |line| "#{ch} #{line}\n" }.join("")
  end

  def run_change_test expected, &blk
    dw = get_writer_class.new FROMCONT, TOCONT
    sb = java.lang.StringBuilder.new
    
    fdc = FileDiffChange.new "text changed", loc_rg(5, 20, 5, 36), loc_rg(6, 20, 6, 33)
    blk.call dw, sb, fdc

    info "sb: #{sb}".green

    assert_equal expected, sb.to_string
  end

  def get_writer_class
  end

  def default_test
    info "self: #{self}"
  end
end
