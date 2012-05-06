#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/fdiff/writers/tc'
require 'diffj/fdiff/writers/ctx_hl'
require 'riel/text'

include Java

class DiffJ::WriterContextHighlightTestCase < DiffJ::WriterTestCase
  def get_writer_class
    DiffJ::FDiff::Writer::ContextHighlightWriter
  end

  def setup
    info "here: #{self}"
    @from_color = nil
    @to_color = nil
  end

  def get_change_expected_from color = "\e[31m"
    expected = ""
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he #{color}durste make avant\e[0m,\n"
    expected << "  He wiste that the man was repentant.\n"
  end

  def get_change_expected_to color = "\e[33m"
    expected = ""
    expected << "  Where he know he would get good payment\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "! For if he gave, he #{color}dared to boast\e[0m,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "  For many a man so hard is of his heart,\n"
    expected << "  He may not weep although him sore smart.\n"
  end

  def test_change_print_from
    run_change_test get_change_expected_from do |dw, str, fdc|
      dw.print_from str, fdc
    end
  end

  def test_change_print_to
    run_change_test get_change_expected_to do |dw, str, fdc|
      dw.print_to str, fdc
    end
  end

  def test_change_print_lines    
    expected = get_change_expected_from
    expected << "\n"
    expected << get_change_expected_to
    expected << "\n"
    
    run_change_test expected do |dw, str, fdc|
      dw.print_lines str, fdc
    end
  end

  def test_added_print_lines
    expected = ""
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "  For if he gave, he dared to boast,\n"
    expected << "  He knew that the man was repentant.\n"
    expected << "! \e[33mFor many a man so hard is of his heart,\e[0m\n"
    expected << "! \e[33mHe may not weep although him sore smart.\e[0m\n"
    expected << "\n"

    run_add_test expected do |dw, str, fda|
      dw.print_lines str, fda
    end
  end

  def test_deleted_print_lines
    expected = ""
    expected << "  And pleasant was his absolution.\n"
    expected << "! \e[31mHe was an easy man to give penance,\e[0m\n"
    expected << "  There as he wist to have a good pittance:\n"
    expected << "  For unto a poor order for to give\n"
    expected << "  Is signe that a man is well y-shrive.\n"
    expected << "\n"

    run_delete_test expected do |dw, str, fda|
      dw.print_lines str, fda
    end
  end

  def get_writer fromcont, tocont
    DiffJ::FDiff::Writer::ContextHighlightWriter.new fromcont, tocont, @from_color, @to_color
  end

  def test_change_print_lines_different_color
    hl = ::Text::ANSIHighlighter.new

    @from_color = hl.code "bold magenta on black"
    @to_color = hl.code "cyan"

    from_code = hl.code @from_color
    to_code = hl.code @to_color

    expected = get_change_expected_from(from_code)
    expected << "\n"
    expected << get_change_expected_to(to_code)
    expected << "\n"
    
    run_change_test expected do |dw, str, fdc|
      dw.print_lines str, fdc
    end
  end
end
