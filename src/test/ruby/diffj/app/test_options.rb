#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/app/options'

include Java

Log::level = Log::DEBUG
Log.set_widths(-15, 5, -50)

class DiffJOptionsTest < Test::Unit::TestCase
  include Loggable
  
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

  def test_default_options
    opts = DiffJ::Options.new
    args = %w{ --version }
    names = opts.process args
    info "opts: #{opts}".bold.green
  end
end
