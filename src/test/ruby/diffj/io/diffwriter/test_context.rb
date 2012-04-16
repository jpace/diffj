#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj/io/diffwriter/context'
require 'diffj/io/diffwriter/tc'

include Java

class DiffJ::WriterContextTestCase < DiffJ::WriterTestCase
  def get_writer_class
    DiffJ::IO::Diff::ContextWriter
  end

  def test_print_from
    expected = ""
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he durste make avant,\n"
    expected << "  He wiste that the man was repentant.\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.ctx_print_from sb, fdc
    end
  end

  def test_print_to
    expected = ""
    expected << "  Where he know he would get good payment\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he dared to boast,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "  For many a man so hard is of his heart,\n"
    expected << "  He may not weep although him sore smart.\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.ctx_print_to sb, fdc
    end
  end

  def test_print_lines
    expected = ""
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he durste make avant,\n"
    expected << "  He wiste that the man was repentant.\n"
    expected << "\n"
    expected << "  Where he know he would get good payment\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he dared to boast,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "  For many a man so hard is of his heart,\n"
    expected << "  He may not weep although him sore smart.\n\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.ctx_print_lines sb, fdc
    end
  end
end
