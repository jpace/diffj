#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/io/diffwriter/tc'

include Java

java_import org.incava.analysis.DetailedReport
java_import org.incava.analysis.FileDiffChange

class DiffJ::WriterNoContextTestCase < DiffJ::WriterTestCase
  include Loggable

  def get_writer_class
    DiffJ::IO::Diff::NoContextWriter
  end
  
  def test_print_from
    run_change_test create_exp_str(FROMCONT[4 .. 4], "<") do |dw, sb, fdc|
      dw.noctx_print_from sb, fdc
    end
  end
  
  def test_print_to
    run_change_test create_exp_str(TOCONT[5 .. 5], ">") do |dw, sb, fdc|
      dw.noctx_print_to sb, fdc
    end
  end

  def test_print_lines
    expected = ""
    expected << create_exp_str(FROMCONT[4 .. 4], "<")
    expected << "---\n"
    expected << create_exp_str(TOCONT[5 .. 5], ">")
    expected << "\n"

    run_change_test expected do |dw, sb, fdc|
      dw.noctx_print_lines sb, fdc
    end
  end
end
