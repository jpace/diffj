#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ImportsDiff

class DiffJ::ImportsSectionTestCase < DiffJ::TestCase
  def subdir
    'imports/section'
  end

  def run_imp_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, subdir, basename
  end

  def added_msg_fmt
    ImportsDiff::IMPORT_SECTION_ADDED
  end  

  def removed_msg_fmt
    ImportsDiff::IMPORT_SECTION_REMOVED
  end

  def section_added from_start, from_end, to_start, to_end
    make_fdiff_add ImportsDiff::IMPORT_SECTION_ADDED, from_start, from_end, to_start, to_end
  end

  def section_removed from_start, from_end, to_start, to_end
    make_fdiff_delete ImportsDiff::IMPORT_SECTION_REMOVED, from_start, from_end, to_start, to_end
  end

  def test_added
    run_imp_test 'Added', section_added(loc(1, 1), loc(1, 5), loc(1, 1), loc(2, 30))
  end

  def test_removed
    run_imp_test 'Removed', section_removed(loc(1, 1), loc(2, 32), loc(1, 1), loc(1, 5))
  end
end
