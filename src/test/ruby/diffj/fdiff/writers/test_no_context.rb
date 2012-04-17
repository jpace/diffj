#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/fdiff/writers/tc'

include Java

class DiffJ::WriterNoContextTestCase < DiffJ::WriterTestCase
  include Loggable

  def get_writer_class
    DiffJ::IO::Diff::NoContextWriter
  end

  def create_exp_from range
    add_lines "", FROMCONT, range, "<"
  end

  def create_exp_to range
    add_lines "", TOCONT, range, ">"
  end
  
  def test_change_print_from
    run_change_test create_exp_from(5 .. 5) do |dw, sb, fdc|
      dw.print_from sb, fdc
    end
  end
  
  def test_change_print_to
    run_change_test create_exp_to(4 .. 4) do |dw, sb, fdc|
      dw.print_to sb, fdc
    end
  end

  def test_change_print_lines
    expected = ""
    expected << create_exp_from(5 .. 5)
    expected << "---\n"
    expected << create_exp_to(4 .. 4)
    expected << "\n"

    run_change_test expected do |dw, sb, fdc|
      dw.print_lines sb, fdc
    end
  end

  def test_added_print_lines
    expected = create_exp_to(6 .. 7)
    expected << "\n"
    
    run_add_test expected do |dw, sb, fda|
      dw.print_lines sb, fda
    end
  end

  def test_deleted_print_lines
    expected = create_exp_from(1 .. 1)
    expected << "\n"
    
    run_delete_test expected do |dw, sb, fda|
      dw.print_lines sb, fda
    end
  end
end
