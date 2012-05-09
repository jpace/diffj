#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/app/options'
require 'tempfile'

include Java

Log::level = Log::DEBUG
Log.set_widths(-15, 5, -50)

class DiffJOptionsTest < Test::Unit::TestCase
  include Loggable
  
  def assert_option allexpvals, key, optval
    assert_equal allexpvals[key], optval, key.to_s.bold
  end

  def assert_options expvals, opts
    allexpvals = default_option_values.merge expvals
    assert_option allexpvals, :brief, opts.show_brief_output
    assert_option allexpvals, :context, opts.show_context_output
    assert_option allexpvals, :highlight, opts.highlight_output
    assert_option allexpvals, :version, opts.show_version
    assert_option allexpvals, :from, opts.from_source
    assert_option allexpvals, :to, opts.to_source
    assert_option allexpvals, :recurse, opts.recurse
    assert_option allexpvals, :first, opts.first_file_name
    assert_option allexpvals, :second, opts.second_file_name
    assert_option allexpvals, :help, opts.show_help
    assert_option allexpvals, :verbose, opts.verbose
    assert_option allexpvals, :from_color, opts.from_color
    assert_option allexpvals, :to_color, opts.to_color
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
    values[:help] = false
    values[:verbose] = false
    values[:from_color] = DiffJ::FDiff::Writer::ContextHighlightWriter::DEFAULT_FROM_COLOR_TEXT
    values[:to_color] = DiffJ::FDiff::Writer::ContextHighlightWriter::DEFAULT_TO_COLOR_TEXT
    values
  end

  def run_test args, exp
    opts = DiffJ::Options.new
    evalstr = "@rcfile = nil"
    opts.instance_eval evalstr
    
    names = opts.process args
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
    # order matters with the optparse implementation:
    # run_test %w{ --context --brief }, { :context => true, :brief => false, :highlight => true }
  end

  def test_highlight
    # highlight turns off brief
    run_test %w{ --highlight }, { :highlight => true, :brief => false }
    run_test %w{ --brief --highlight }, { :highlight => true, :brief => false }
    # order matters with the optparse implementation:
    # run_test %w{ --highlight --brief }, { :highlight => true, :brief => false }
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

  def test_help
    run_test %w{ -h --help }, { :help => true }
  end

  def test_verbose
    run_test %w{ --verbose }, { :verbose => true }
  end

  def test_names
    run_test %w{ --verbose abc xyz }, { :verbose => true }
  end

  def run_rcfile_test args, exp
    tf = Tempfile.new Pathname(__FILE__).basename.to_s
    info "tf.path: #{tf}"

    args.each do |name, value|
      info "name: #{name}"
      info "value: #{value}"
      tf.puts "#{name}: #{value}"
    end

    tf.flush

    puts tf.readlines

    opts = DiffJ::Options.new
    # evalstr = "@rcfile = '" + tf.path + "'"
    # opts.instance_eval evalstr

    opts.parse_from_rcfile tf.path

    assert_options exp, opts
  end

  def test_rcfile_default
    args = []
    exp = { }
    run_rcfile_test [], exp
  end

  def test_rcfile_option_from_source
    args = [
            %w{ from-source 1.6 }
           ]
    exp = { :from => "1.6" }    
    run_rcfile_test args, exp
  end

  def test_rcfile_option_highlight
    args = [
            %w{ highlight true }
           ]
    exp = { :highlight => true }
    run_rcfile_test args, exp
  end

  def test_rcfile_option_highlight_false
    args = [
            %w{ highlight false }
           ]
    exp = { :highlight => false }    
    run_rcfile_test args, exp
  end

  def test_rcfile_option_multiples
    args = [
            %w{ context true },
            %w{ source 1.4 }
           ]
    exp = { 
      :brief => false,
      :context => true,
      :from => "1.4",
      :to => "1.4",
      :highlight => true,
    }
    run_rcfile_test args, exp
  end

  def test_rcfile_option_context_highlight_off
    args = [
            %w{ context true },
            %w{ highlight false },
           ]
    exp = { 
      :brief => false,
      :context => true,
      :highlight => false,
    }
    run_rcfile_test args, exp
  end

  def test_rcfile_option_context_colors
    args = [
            %w{ context true },
            %w{ highlight true },
            [ 'from-color', 'bold blue on green' ],
            [ 'to-color', 'underscore magenta on cyan' ],
           ]
    exp = { 
      :brief => false,
      :context => true,
      :highlight => true,
      :from_color => 'bold blue on green',
      :to_color => 'underscore magenta on cyan',
    }
    run_rcfile_test args, exp
  end
end
