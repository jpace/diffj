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

  def assert_option allexpvals, key, optval
    assert_equal allexpvals[key], optval, key.to_s.bold
  end

  def assert_options expvals, opts
    allexpvals = default_option_values.merge expvals
    assert_option allexpvals, :brief, opts.showBriefOutput()
    assert_option allexpvals, :context, opts.showContextOutput()
    assert_option allexpvals, :highlight, opts.highlightOutput()
    assert_option allexpvals, :version, opts.showVersion()
    assert_option allexpvals, :from, opts.getFromSource()
    assert_option allexpvals, :to, opts.getToSource()
    assert_option allexpvals, :recurse, opts.recurse()
    assert_option allexpvals, :first, opts.getFirstFileName()
    assert_option allexpvals, :second, opts.getSecondFileName()
  end

  def default_option_values
    values = Hash.new
    values[:brief] = false
    values[:context] = false
    values[:highlight] = false
    values[:version] = false
    values[:from] = "1.5"
    values[:to] = "1.5"
    values[:recurse] = false
    values[:first] = nil
    values[:second] = nil
    values
  end

  def run_test args, exp
    opts = DiffJ::Options.new
    names = opts.process args
    info "opts: #{opts}".bold.green
    assert_options exp, opts
  end

  def test_version
    run_test %w{ --version }, { :version => true }
    run_test %w{ -v }, { :version => true }
  end

  def test_brief
    run_test %w{ --brief }, { :brief => true }
  end

  def test_context
    # context sets highlight on and brief off
    run_test %w{ --context }, { :context => true, :brief => false, :highlight => true }
    run_test %w{ --brief --context }, { :context => true, :brief => false, :highlight => true }
    run_test %w{ --context --brief }, { :context => true, :brief => false, :highlight => true }
  end

  def test_highlight
    # highlight turns off brief
    run_test %w{ --highlight }, { :highlight => true, :brief => false }
    run_test %w{ --brief --highlight }, { :highlight => true, :brief => false }
    # order doesn't matter:
    run_test %w{ --highlight --brief }, { :highlight => true, :brief => false }
  end

  def test_recurse
    run_test %w{ --recurse }, { :recurse => true }
    run_test %w{ -r }, { :recurse => true }
  end

  def test_from_source
    run_test %w{ }, { :from => "1.5" }
    run_test %w{ --from-source 1.4 }, { :from => "1.4" }
  end

  def test_to_source
    run_test %w{ }, { :to => "1.5" }
    run_test %w{ --to-source 1.4 }, { :to => "1.4" }
  end

  def test_source
    run_test %w{ }, { :from => "1.5" }
    run_test %w{ }, { :to => "1.5" }
    run_test %w{ --source 1.4 }, { :from => "1.4", :to => "1.4" }
  end

  def test_unified_format
    # this is for svn diff --diff-cmd cmd, which passes "-u, -L first, -L second, file1, file2":

    # ignored for now:
    run_test %w{ -u }, { }
  end

  def test_from_and_to_names
    # this is for svn diff --diff-cmd cmd, which passes "-u, -L first, -L second, file1, file2":

    %w{ -L --name }.each do |nametag|
      run_test [ nametag, "Abc.java" ], { :first => "Abc.java", :second => nil }
      run_test [ '-u', nametag, "Abc.java" ], { :first => "Abc.java", :second => nil }

      %w{ -L --name }.each do |secondtag|
        run_test [ nametag, "Abc.java", secondtag, "Xyz.java" ], { :first => "Abc.java", :second => "Xyz.java" }
        run_test [ '-u', nametag, "Abc.java", secondtag, "Xyz.java" ], { :first => "Abc.java", :second => "Xyz.java" }
      end
    end
  end
end
