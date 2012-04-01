#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

class DiffJTypeChangeTestCase < DiffJTypeTestCase
  def subdir
    'typechange'
  end

  def type_iface_to_cls from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_INTERFACE_TO_CLASS, from_start, from_end, to_start, to_end
  end

  def type_cls_to_iface from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, from_start, from_end, to_start, to_end
  end

  def test_type_interface_to_class
    run_test 'TypeChgIfaceCls', type_iface_to_cls(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
  end

  def test_type_class_to_interface
    run_test 'TypeChgClsIface', type_cls_to_iface(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
  end
end
