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

class DiffJ::ReportTestCase < Test::Unit::TestCase
  include Loggable

  def loc_rg from_line, from_col, to_line, to_col
    from = Location.new from_line, from_col
    to = Location.new to_line, to_col
    
    LocationRange.new from, to
  end

  def create_report sw, show_context, highlight
  end
  
  def run_report show_context, highlight
    sw = java.io.StringWriter.new
    rpt = create_report sw, show_context, highlight
    
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

    # it looks like JRuby has a limit of three method parameters for lookup,
    # so we do it ourself here:
    method_params = [ java.lang.String, java.lang.String, java.lang.String, java.lang.String ]
    # rpt.java_send :reset, method_params, "-", fromcont.join("\n"), "-", tocont.join("\n")

    info "rpt: #{rpt}; #{rpt.class}".on_magenta

    rpt.reset "-", fromcont.join("\n"), "-", tocont.join("\n")

    filediffs = rpt.differences

    filediffs.add DiffJ::FDiffChange.new "text changed", :locranges => [ loc_rg(1, 9, 1, 9), loc_rg(1, 9, 1, 23) ]
    filediffs.add DiffJ::FDiffAdd.new "line(s) inserted", :locranges => [ loc_rg(2, 1, 2, 1), loc_rg(2, 1, 3, 23) ]
    filediffs.add DiffJ::FDiffDelete.new "line(s) removed", :locranges => [ loc_rg(3, 1, 3, 11), loc_rg(5, 1, 5, 1) ]

    info "filediffs: #{filediffs}".on_red
    
    rpt.flush

    assert filediffs.empty?

    info "sw: <<<#{sw}>>>"
    
    sw.to_string
  end

  def assert_report_result expected, show_context, highlight
    str = run_report show_context, highlight
    info "str: #{str}"
    assert_equal expected.collect { |x| "#{x}\n" }.join(''), str
  end

  # this testcase has no tests of its own:
  def default_test
    info "self: #{self}".cyan
  end
end
