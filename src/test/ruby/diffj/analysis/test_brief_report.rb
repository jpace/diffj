#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/analysis/tc_report'

include Java

java_import org.incava.analysis.BriefReport

class DiffJ::BriefReportTestCase < DiffJ::ReportTestCase

  def create_report sw, show_context, highlight
    BriefReport.new sw
  end    
  
  def test_all
    expected  = Array.new
    expected << "- <=> -"
    expected << "1c1: text changed"
    expected << "2a2,3: line(s) inserted"
    expected << "3d5: line(s) removed"

    assert_report_result expected, false, false
  end  
end
