#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'

include Java

class DiffJTestCase < Test::Unit::TestCase
  include Loggable
  
  TESTBED_DIR = '/proj/org/incava/diffj/src/test/resources'
  
  def run_test dirname
    fnames = %w{ d0 d1 }.collect { |subdir| TESTBED_DIR + '/' + dirname + '/' + subdir }
    brief = false
    context = true
    highlight = true
    recurse = true
    fromname = nil
    fromver = "1.5"
    toname = nil
    tover = "1.5"
    
    diffj = DiffJ::CLI.new brief, context, highlight, recurse, fromname, fromver, toname, tover
    diffj.process_names fnames
    assert_not_nil diffj
  end

  def run_fdiff_test expected_fdiffs, dirname, basename
    brief = false
    context = true
    highlight = true
    recurse = true
    fromname = nil
    fromver = "1.5"
    toname = nil
    tover = "1.5"
    
    diffj = DiffJ::CLI.new brief, context, highlight, recurse, fromname, fromver, toname, tover

    report = diffj.report
    info "report: #{report}"
    info "report.differences: #{report.differences}"

    fnames = %w{ d0 d1 }.collect { |subdir| TESTBED_DIR + '/' + dirname + '/' + subdir + '/' + basename + '.java' }

    fromname, toname = *fnames
    info "fromname: #{fromname}"
    info "toname: #{toname}"

    fromfile = diffj.create_from_element fromname
    tofile = diffj.create_to_element toname

    info "report: #{report}"
    info "report.differences: #{report.differences}"

    fromfile.compare report, tofile

    info "report: #{report}"
    info "report.differences: #{report.differences}"

    actual_fdiffs = report.differences

    info "expected_fdiffs: #{expected_fdiffs.class}"
    info "actual_fdiffs: #{actual_fdiffs.class}"

    if expected_fdiffs.length != actual_fdiffs.length
      info "mismatched number of fdiffs".red
      maxlen = [ expected_fdiffs.length, actual_fdiffs.length ].max
      (0 ... maxlen).each do |fdidx|
        info "expected_fdiffs[#{fdidx}]: #{expected_fdiffs[fdidx]}; #{expected_fdiffs[fdidx].class}"
      end
      assert_equal expected_fdiffs.length, actual_fdiffs.length, "mismatched number of fdiffs"
    else
      actual_fdiffs.each_with_index do |actfdiff, fdidx|
        expfdiff = expected_fdiffs[fdidx]
        assert_fdiffs_equal expfdiff, actfdiff, "fdiff[#{fdidx}]"
      end      
    end    
  end

  def assert_equal exp, act, msg
    begin
      super
    rescue => e
      info "msg: #{msg}".red
      info "exp: #{exp}".red
      info "exp: #{exp.class}".red
      info "act: #{act}".red
      info "act: #{act.class}".red      
      raise e
    end
  end

  def assert_fdiffs_equal exp, act, msg = ""
    info "exp.type: #{exp.type}"
    info "act.type: #{act.type}"
    assert_equal exp.type, act.type, msg + ".type"
    assert_equal exp.message, act.message, msg + ".message"


    assert_equal exp.first_location, act.first_location, msg + ".first_location"

    info "exp.second_location: #{exp.second_location}"
    info "act.second_location: #{act.second_location}"

    assert_equal exp.second_location, act.second_location, msg + ".second_location"
  end

  def loc x, y
    org.incava.ijdk.text.Location.new x, y
  end

  def loctext loc, text
    loc loc.line, loc.column + text.length - 1
  end

  def test_nothing
  end

  def format msg, *values
    java.text.MessageFormat.format msg, *values
  end

  def make_fdiff type, msgvals, from_start, from_end, to_start, to_end
    msg = msgvals.kind_of?(Array) && msgvals.length > 1 ? format(msgvals[0], *(msgvals[1 .. -1])) : msgvals
    type.new(msg, from_start, from_end, to_start, to_end)
  end

  def make_fdiff_add msgvals, from_start, from_end, to_start, to_end
    make_fdiff org.incava.analysis.FileDiffAdd, msgvals, from_start, from_end, to_start, to_end
  end

  def make_fdiff_change msgvals, from_start, from_end, to_start, to_end
    make_fdiff org.incava.analysis.FileDiffChange, msgvals, from_start, from_end, to_start, to_end
  end

  def make_fdiff_delete msgvals, from_start, from_end, to_start, to_end
    make_fdiff org.incava.analysis.FileDiffDelete, msgvals, from_start, from_end, to_start, to_end
  end
end
