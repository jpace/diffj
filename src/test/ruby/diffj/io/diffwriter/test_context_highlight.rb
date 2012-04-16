#!/usr/bin/jruby -w
# -*- ruby -*-

require 'test/unit'
require 'java'
require 'rubygems'
require 'riel'
require 'diffj'
require 'diffj/io/diffwriter/writer'
require 'diffj/io/diffwriter/context_highlight'
require 'diffj/io/diffwriter/tc'

include Java

class DiffJ::WriterContextHighlightTestCase < DiffJ::WriterTestCase
  def get_writer_class
    DiffJ::IO::Diff::CtxHighltWriter
  end

  def test_print_from
    expected = ""
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he \e[31mdurste make avant\e[0m,\n"
    expected << "  He wiste that the man was repentant.\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.printFrom sb, fdc
    end
  end

  def test_print_to
    expected = ""
    expected << "  Where he know he would get good payment\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he \e[33mdared to boast\e[0m,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "  For many a man so hard is of his heart,\n"
    expected << "  He may not weep although him sore smart.\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.printTo sb, fdc
    end
  end

  def test_print_lines
    expected = ""
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he \e[31mdurste make avant\e[0m,\n"
    expected << "  He wiste that the man was repentant.\n"
    expected << "\n"
    expected << "  Where he know he would get good payment\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he \e[33mdared to boast\e[0m,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "  For many a man so hard is of his heart,\n"
    expected << "  He may not weep although him sore smart.\n\n"
    
    run_change_test expected do |dw, sb, fdc|
      dw.printLines sb, fdc
    end
  end
end
