#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ImportsDiff

class DiffJImportsTestCase < DiffJTestCase
  def run_imp_test expected_fdiffs, basename
    run_fdiff_test expected_fdiffs, 'impdiffs', basename
  end

  def section_added from_start, from_end, to_start, to_end
    make_fdiff_add ImportsDiff::IMPORT_SECTION_ADDED, from_start, from_end, to_start, to_end
  end

  def section_removed from_start, from_end, to_start, to_end
    make_fdiff_delete ImportsDiff::IMPORT_SECTION_REMOVED, from_start, from_end, to_start, to_end
  end

  def import_added name, from_start, from_end, to_start, to_end
    make_fdiff_add format(ImportsDiff::IMPORT_ADDED, name), from_start, from_end, to_start, to_end
  end

  def import_removed name, from_start, from_end, to_start, to_end
    make_fdiff_delete format(ImportsDiff::IMPORT_REMOVED, name), from_start, from_end, to_start, to_end
  end

  def test_section_added
    expected_fdiffs = Array.new
    expected_fdiffs << section_added(loc(1, 1), loc(1, 5), loc(1, 1), loc(2, 30))
    run_imp_test expected_fdiffs, 'ImpSctnAdded'
  end

  def test_section_removed
    expected_fdiffs = Array.new
    expected_fdiffs << section_removed(loc(1, 1), loc(2, 32), loc(1, 1), loc(1, 5))
    run_imp_test expected_fdiffs, 'ImpSctnRemoved'
  end

  def test_import_added
    name = 'org.incava.Added'
    expected_fdiffs = Array.new
    expected_fdiffs << import_added(name, loc(1, 1), loc(1, 27), loc(2, 1), loc(2, 24))
    run_imp_test expected_fdiffs, 'ImpAdded'
  end

  def test_section_removed
    name = "org.incava.Removed"
    expected_fdiffs = Array.new
    expected_fdiffs << import_removed(name, loc(2, 1), loc(2, 26), loc(1, 1), loc(1, 27))
    run_imp_test expected_fdiffs, 'ImpRemoved'
  end
end
