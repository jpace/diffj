#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.TypesDiff

class DiffJTypesTestCase < DiffJTestCase
  def run_type_test expected_fdiffs, basename
    run_fdiff_test expected_fdiffs, 'typesdiffs', basename
  end

  def type_added name, from_start, from_end, to_start, to_end
    make_fdiff_add format(TypesDiff::TYPE_DECLARATION_ADDED, name), from_start, from_end, to_start, to_end
  end

  def type_removed name, from_start, from_end, to_start, to_end
    make_fdiff_delete format(TypesDiff::TYPE_DECLARATION_REMOVED, name), from_start, from_end, to_start, to_end
  end

  def test_type_added
    name = 'TheTypeAdded'
    expected_fdiffs = Array.new
    expected_fdiffs << type_added(name, loc(1, 1), loc(2, 2), loc(4, 1), loc(5, 1))
    run_type_test expected_fdiffs, 'TypeAdded'
  end

  def test_section_removed
    name = "TheTypeRemoved"
    expected_fdiffs = Array.new
    expected_fdiffs << type_removed(name, loc(4, 1), loc(5, 1), loc(1, 1), loc(2, 2))
    run_type_test expected_fdiffs, 'TypeRemoved'
  end
end
