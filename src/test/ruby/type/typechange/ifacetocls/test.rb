#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

class DiffJ::TypeChangeIfaceToClsTestCase < DiffJTypeTestCase
  def subdir
    'typechange/ifacetocls'
  end

  def type_iface_to_cls from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_INTERFACE_TO_CLASS, from_start, from_end, to_start, to_end
  end

  def test_changed
    run_test 'Changed', type_iface_to_cls(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
  end
end
