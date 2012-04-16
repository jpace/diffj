#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/analysis/fdiff'
require 'diffj/io/diffwriter/writer'
require 'diffj/io/diffwriter/no_context'

include Java

java_import org.incava.analysis.DetailedReport
java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

class DiffJ::WriterTestCase < Test::Unit::TestCase
  include Loggable

  FROMCONT  = Array.new
  #           0         1         2         3         4         5
  #            12345678901234567890123456789012345678901234567890
  FROMCONT << "And pleasant was his absolution."                   # 1
  FROMCONT << "He was an easy man to give penance,"  # delete      # 2
  FROMCONT << "There as he wist to have a good pittance:" # change # 3
  FROMCONT << "For unto a poor order for to give"                  # 4
  FROMCONT << "Is signe that a man is well y-shrive."              # 5
  FROMCONT << "For if he gave, he durste make avant," # change     # 6
  FROMCONT << "He wiste that the man was repentant."  # change     # 7

  TOCONT    = Array.new
  #           0         1         2         3         4         5
  #            12345678901234567890123456789012345678901234567890
  TOCONT   << "And pleasant was his absolution."                   # 1
  # TOCONT   << "He was an easy man to give penance,"           
  TOCONT   << "Where he know he would get good payment"            # 2
  TOCONT   << "For unto a poor order for to give"                  # 3
  TOCONT   << "Is signe that a man is well y-shrive."              # 4
  TOCONT   << "For if he gave, he dared to boast,"       # change  # 5
  TOCONT   << "He knew that the man was repentant."      # change  # 6
  TOCONT   << "For many a man so hard is of his heart,"  # add     # 7
  TOCONT   << "He may not weep although him sore smart." # add     # 8

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def create_exp_str lines, ch
    lines.collect { |line| "#{ch} #{line}\n" }.join("")
  end

  def run_delta_test expected, fdiff, &blk
    dw = get_writer_class.new FROMCONT, TOCONT
    sb = java.lang.StringBuilder.new
    
    blk.call dw, sb, fdiff

    info "sb: #{sb}".green

    assert_equal expected, sb.to_string
  end

  def run_change_test expected, &blk
    fdc = DiffJ::FDiffChange.new "text changed", :locranges => [ loc_rg(6, 20, 6, 36), loc_rg(5, 20, 5, 33) ]
    run_delta_test expected, fdc, &blk
  end

  def run_add_test expected, &blk
    fda = org.incava.analysis.FileDiffAdd.new "text added", loc_rg(6, 1, 6, 1), loc_rg(7, 1, 8, 40)
    run_delta_test expected, fda, &blk
  end

  def run_delete_test expected, &blk
    fda = org.incava.analysis.FileDiffDelete.new "text deleted", loc_rg(2, 1, 2, 35), loc_rg(2, 1, 2, 1)
    run_delta_test expected, fda, &blk
  end

  def get_writer_class
  end

  def default_test
    info "self: #{self}"
  end
end
