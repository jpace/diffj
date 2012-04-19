#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/fdiff/reports/tc'
require 'diffj/fdiff/reports/short'

include Java

class DiffJ::ShortReportTestCase < DiffJ::ReportTestCase
  def create_report sio, show_context, highlight
    DiffJ::FDiff::Report::ShortReport.new sio
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
