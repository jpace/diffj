#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/tc'
require 'diffj/fdiff/fdiff'
require 'stringio'

include Java

class DiffJ::ReportTestCase < DiffJ::TestCase
  include Loggable

  def create_report sio, show_context, highlight
  end
  
  def run_report show_context, highlight
    sio = StringIO.new
    rpt = create_report sio, show_context, highlight
    
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

    rpt.reset "-", fromcont.join("\n"), "-", tocont.join("\n")

    filediffs = rpt.differences

    filediffs.add DiffJ::FDiffChange.new "text changed", :locranges => [ locrg(1, 9, 1, 9), locrg(1, 9, 1, 23) ]
    filediffs.add DiffJ::FDiffAdd.new "line(s) inserted", :locranges => [ locrg(2, 1, 2, 1), locrg(2, 1, 3, 23) ]
    filediffs.add DiffJ::FDiffDelete.new "line(s) removed", :locranges => [ locrg(3, 1, 3, 11), locrg(5, 1, 5, 1) ]

    info "filediffs: #{filediffs}"
    
    rpt.flush

    assert filediffs.empty?

    info "sio: <<<#{sio.string}>>>"
    
    sio.string
  end

  def assert_report_result expected, show_context, highlight
    str = run_report show_context, highlight
    info "str: #{str}"
    assert_equal expected.collect { |x| "#{x}\n" }.join(''), str
  end

  # this testcase has no tests of its own:
  def default_test
    info "self: #{self}"
  end
end
