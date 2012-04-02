#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.TypesDiff

class DiffJ::TypeChangeClsToIfaceTestCase < DiffJTypeTestCase
  def subdir
    'typechange/clstoiface'
  end

  def type_cls_to_iface from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, from_start, from_end, to_start, to_end
  end

  def test_changed
    run_test 'Changed', type_cls_to_iface(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
  end
end
