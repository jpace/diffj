#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/io/diffwriter/tc'

include Java

class DiffJ::WriterNoContextTestCase < DiffJ::WriterTestCase
  include Loggable

  def get_writer_class
    DiffJ::IO::Diff::NoContextWriter
  end
  
  def test_change_print_from
    run_change_test create_exp_str(FROMCONT[5 .. 5], "<") do |dw, sb, fdc|
      dw.noctx_print_from sb, fdc
    end
  end
  
  def test_change_print_to
    run_change_test create_exp_str(TOCONT[4 .. 4], ">") do |dw, sb, fdc|
      dw.noctx_print_to sb, fdc
    end
  end

  def test_change_print_lines
    expected = ""
    expected << create_exp_str(FROMCONT[5 .. 5], "<")
    expected << "---\n"
    expected << create_exp_str(TOCONT[4 .. 4], ">")
    expected << "\n"

    run_change_test expected do |dw, sb, fdc|
      dw.noctx_print_lines sb, fdc
    end
  end
end
