#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/io/writer'

include Java

java_import org.incava.analysis.DetailedReport
java_import org.incava.analysis.FileDiffChange
java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

class DiffJ::WriterNoContextTestCase < Test::Unit::TestCase
  include Loggable

  FROMCONT  = Array.new
  FROMCONT << "line 1: one two three four"
  FROMCONT << "2nd line: five six seven eight"
  FROMCONT << "line three: nine ten eleven"
  
  TOCONT    = Array.new
  TOCONT   << "alpha bravo charlie delta (line 1)"
  TOCONT   << "echo foxtrot golf hotel (2d line)"
  TOCONT   << "india juliet kilo (#3 line)"

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def create_exp_str lines, ch
    lines.collect { |line| "#{ch} #{line}\n" }.join("")
  end

  def run_change_test expected, &blk
    dw = DiffJ::IO::Diff::NoContextWriter.new FROMCONT, TOCONT
    sb = java.lang.StringBuilder.new
    
    fdc = FileDiffChange.new "text changed", loc_rg(1, 1, 2, 4), loc_rg(3, 1, 3, 8)
    blk.call dw, sb, fdc

    info "sb: #{sb}".green

    assert_equal expected, sb.to_string
  end
  
  def test_print_from
    run_change_test create_exp_str(FROMCONT[0 .. 1], "<") do |dw, sb, fdc|
      dw.noctx_print_from sb, fdc
    end
  end
  
  def test_print_to
    run_change_test create_exp_str(TOCONT[2 .. 2], ">") do |dw, sb, fdc|
      dw.noctx_print_to sb, fdc
    end
  end

  def test_print_lines
    expected = ""
    expected << create_exp_str(FROMCONT[0 .. 1], "<")
    expected << "---\n"
    expected << create_exp_str(TOCONT[2 .. 2], ">")
    expected << "\n"

    run_change_test expected do |dw, sb, fdc|
      dw.noctx_print_lines sb, fdc
    end
  end
end
