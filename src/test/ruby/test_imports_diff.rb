#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ImportsDiff

class DiffJImportsTestCase < DiffJTestCase
  def subdir
    'impdiffs'
  end

  def run_imp_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, subdir, basename
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
    run_imp_test 'ImpSctnAdded', section_added(loc(1, 1), loc(1, 5), loc(1, 1), loc(2, 30))
  end

  def test_section_removed
    run_imp_test 'ImpSctnRemoved', section_removed(loc(1, 1), loc(2, 32), loc(1, 1), loc(1, 5))
  end

  def test_import_added
    run_imp_test 'ImpAdded', import_added('org.incava.Added', loc(1, 1), loc(1, 27), loc(2, 1), loc(2, 24))
  end

  def test_section_removed
    run_imp_test 'ImpRemoved', import_removed('org.incava.Removed', loc(2, 1), loc(2, 26), loc(1, 1), loc(1, 27))
  end
end
