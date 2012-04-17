#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/fdiff/writers/tc'
require 'diffj/fdiff/writers/context'

include Java

class DiffJ::WriterContextTestCase < DiffJ::WriterTestCase
  def get_writer_class
    DiffJ::IO::Diff::ContextWriter
  end

  def test_change_print_from
    expected = make_expected FROMCONT, 2 .. 4, 5 .. 5, 6 .. 6
    run_change_test expected do |dw, sb, fdc|
      dw.print_from sb, fdc
    end
  end

  def test_change_print_to
    expected = make_expected TOCONT, 1 .. 3, 4 .. 4, 5 .. 7
    run_change_test expected do |dw, sb, fdc|
      dw.print_to sb, fdc
    end
  end

  def test_change_print_lines
    expfrom = make_expected FROMCONT, 2 .. 4, 5 .. 5, 6 .. 6
    expto   = make_expected TOCONT,   1 .. 3, 4 .. 4, 5 .. 7
    expected = expfrom + "\n" + expto + "\n"
    run_change_test expected do |dw, sb, fdc|
      dw.print_lines sb, fdc
    end
  end

  def make_expected lines, pre, match, post
    expected  = ""
    add_lines expected, lines, pre
    add_lines expected, lines, match, "!"
    add_lines expected, lines, post
    expected
  end

  def test_added_print_lines
    expected = make_expected TOCONT, 3 .. 5, 6 .. 7, nil
    expected << "\n"
    
    run_add_test expected do |dw, sb, fda|
      dw.print_lines sb, fda
    end
  end

  def test_deleted_print_lines
    expected = make_expected FROMCONT, 0 .. 0, 1 .. 1, 2 .. 4
    expected << "\n"
    
    run_delete_test expected do |dw, sb, fdd|
      dw.print_lines sb, fdd
    end
  end
end
