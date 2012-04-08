#!/usr/bin/jruby -w
# -*- ruby -*-

require 'imports/tc'

include Java

class DiffJ::Imports::SectionTestCase < DiffJ::Imports::TestCase
  def added_msg_fmt
    DiffJ::ImportsComparator::IMPORT_SECTION_ADDED
  end  

  def removed_msg_fmt
    DiffJ::ImportsComparator::IMPORT_SECTION_REMOVED
  end

  def added from_start, from_end, to_start, to_end
    make_fdiff_add added_msg_fmt, from_start, from_end, to_start, to_end
  end

  def removed from_start, from_end, to_start, to_end
    make_fdiff_delete removed_msg_fmt, from_start, from_end, to_start, to_end
  end

  def test_added
    run_imp_test 'Added', added(loc(1, 1), loc(1, 5), loc(1, 1), loc(2, 30))
  end

  def test_removed
    run_imp_test 'Removed', removed(loc(1, 1), loc(2, 32), loc(1, 1), loc(1, 5))
  end
end
