#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/fdiff/tc_report'
require 'diffj/fdiff/long_report'

include Java

java_import org.incava.analysis.DetailedReport

class DiffJ::LongReportTestCase < DiffJ::ReportTestCase
  def create_report sw, show_context, highlight
    DiffJ::Analysis::LongReport.new sw, show_context, highlight
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
