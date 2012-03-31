#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJTypeClsAccessTestCase < DiffJTestCase
  def run_type_test expected_fdiffs, basename
    run_fdiff_test expected_fdiffs, 'typediffs', basename
  end

  def type_added_access access, from_start, from_end, to_start
    make_fdiff_change format(ItemDiff::ACCESS_ADDED, access), from_start, from_end, to_start, loctext(to_start, access)
  end

  def type_changed_access from_access, to_access, from_start, to_start
    make_fdiff_change format(ItemDiff::ACCESS_CHANGED, from_access, to_access), from_start, loctext(from_start, from_access), to_start, loctext(to_start, to_access)
  end
  
  def type_removed_access access, from_start, to_start, to_end
    make_fdiff_change format(ItemDiff::ACCESS_REMOVED, access), from_start, loctext(from_start, access), to_start, to_end
  end

  def type_cls_to_iface from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, from_start, from_end, to_start, to_end
  end
  
  def test_public_to_none
    expected_fdiffs = Array.new
    expected_fdiffs << type_removed_access("public", loc(1, 1), loc(1, 1), loc(1, 5))
    run_fdiff_test expected_fdiffs, 'typediffs/clsaccess', 'ClsAccessPublicToNone'
  end

  def test_none_to_public
    expected_fdiffs = Array.new
    expected_fdiffs << type_added_access("public", loc(1, 1), loc(1, 5), loc(1, 1))
    run_fdiff_test expected_fdiffs, 'typediffs/clsaccess', 'ClsAccessNoneToPublic'
  end

  def test_public_to_protected
    expected_fdiffs = Array.new
    expected_fdiffs << type_changed_access("public", "protected", loc(1, 1), loc(1, 1))
    run_fdiff_test expected_fdiffs, 'typediffs/clsaccess', 'ClsAccessPublicToProtected'
  end
end
