#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'

include Java

java_import org.incava.analysis.DetailedReport
java_import org.incava.analysis.FileDiffAdd
java_import org.incava.analysis.FileDiffChange
java_import org.incava.analysis.FileDiffDelete
java_import org.incava.ijdk.text.Location
java_import org.incava.ijdk.text.LocationRange

class DiffJ::ReportTestCase < Test::Unit::TestCase
  include Loggable

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def run_report show_context, highlight
    sw = java.io.StringWriter.new
    dr = DetailedReport.new sw, show_context, highlight
    info "dr: #{dr}".on_red
    info "sw: #{sw}".on_red
    
    fromcont = Array.new
    #            columns and lines are 1-indexed
    #            0        1         2         3         4         5
    #            12345678901234567890123456789012345678901234567890
    fromcont << "line one."
    fromcont << "line two."
    fromcont << "line three."
    fromcont << "line four."
    fromcont << "line five."

    tocont    = Array.new
    #            0        1         2         3         4         5
    #            12345678901234567890123456789012345678901234567890
    tocont   << "line one, which changed."
    tocont   << "inserted/added line one"
    tocont   << "inserted/added line two"
    tocont   << "line two"
    tocont   << "line four"
    tocont   << "a change applied to line five."

    dr.reset fromcont.join("\n"), tocont.join("\n")

    filediffs = dr.differences

    filediffs.add FileDiffChange.new "text changed", loc_rg(1, 9, 1, 9), loc_rg(1, 9, 1, 23)
    filediffs.add FileDiffAdd.new "line(s) inserted", loc_rg(2, 1, 2, 1), loc_rg(2, 1, 3, 23)
    filediffs.add FileDiffDelete.new "line(s) removed", loc_rg(3, 1, 3, 11), loc_rg(5, 1, 5, 1)

    info "filediffs: #{filediffs}".on_red
    
    dr.flush

    assert filediffs.empty?

    info "sw: <<<#{sw}>>>"
    
    sw.to_string
  end

  def assert_report_result expected, show_context, highlight
    str = run_report show_context, highlight
    info "str: #{str}"
    assert_equal expected.collect { |x| "#{x}\n" }.join(''), str
  end
  
  def test_no_context
    expected  = Array.new
    expected << "- <=> -"
    expected << "1c1 text changed"
    expected << "< line one."
    expected << "---"
    expected << "> line one, which changed."
    expected << ""
    expected << "2a2,3 line(s) inserted"
    expected << "> inserted/added line one"
    expected << "> inserted/added line two"
    expected << ""
    expected << "3d5 line(s) removed"
    expected << "< line three."
    expected << ""

    assert_report_result expected, false, false
  end
  
  def test_context_no_highlight
    expected  = Array.new
    expected << "- <=> -"
    expected << "1c1 text changed"
    expected << "! line one."
    expected << "  line two."
    expected << "  line three."
    expected << "  line four."
    expected << ""
    expected << "! line one, which changed."
    expected << "  inserted/added line one"
    expected << "  inserted/added line two"
    expected << "  line two"
    expected << ""
    expected << "2a2,3 line(s) inserted"
    expected << "  line one, which changed."
    expected << "! inserted/added line one"
    expected << "! inserted/added line two"
    expected << "  line two"
    expected << "  line four"
    expected << "  a change applied to line five."
    expected << ""
    expected << "3d5 line(s) removed"
    expected << "  line one."
    expected << "  line two."
    expected << "! line three."
    expected << "  line four."
    expected << "  line five."
    expected << ""

    assert_report_result expected, true, false
  end

  def adorn str, color
    str.send color
  end
  
  def test_context_highlight
    expected  = Array.new

    expected << "- <=> -"
    expected << "1c1 text changed"
    expected << "! line one" + adorn(".", :red) + ""
    expected << "  line two."
    expected << "  line three."
    expected << "  line four."
    expected << ""
    expected << "! line one" + adorn(", which changed", :yellow) + "."
    expected << "  inserted/added line one"
    expected << "  inserted/added line two"
    expected << "  line two"
    expected << ""
    expected << "2a2,3 line(s) inserted"
    expected << "  line one, which changed."
    expected << "! " + adorn("inserted/added line one", :yellow)
    expected << "! " + adorn("inserted/added line two", :yellow)
    expected << "  line two"
    expected << "  line four"
    expected << "  a change applied to line five."
    expected << ""
    expected << "3d5 line(s) removed"
    expected << "  line one."
    expected << "  line two."
    expected << "! " + adorn("line three.", :red)
    expected << "  line four."
    expected << "  line five."
    expected << ""

    assert_report_result expected, true, true
  end
end
