#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffjtestcase'

include Java

class DiffJTypeChangeTestCase < DiffJTestCase
  def run_type_test expected_fdiffs, basename
    run_fdiff_test expected_fdiffs, 'typediffs/typechange', basename
  end

  def type_iface_to_cls from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_INTERFACE_TO_CLASS, from_start, from_end, to_start, to_end
  end

  def type_cls_to_iface from_start, from_end, to_start, to_end
    make_fdiff_change TypesDiff::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, from_start, from_end, to_start, to_end
  end

  def test_type_interface_to_class
    expected_fdiffs = Array.new
    expected_fdiffs << type_iface_to_cls(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
    run_type_test expected_fdiffs, 'TypeChgIfaceCls'
  end

  def test_type_class_to_interface
    expected_fdiffs = Array.new
    expected_fdiffs << type_cls_to_iface(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
    run_type_test expected_fdiffs, 'TypeChgClsIface'
  end
end
