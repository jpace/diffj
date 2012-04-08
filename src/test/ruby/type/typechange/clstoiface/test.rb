#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'
require 'diffj/ast/types'

include Java

class DiffJ::TypeChangeClsToIfaceTestCase < DiffJTypeTestCase
  def subdir
    'typechange/clstoiface'
  end

  def type_cls_to_iface from_start, from_end, to_start, to_end
    msg =  DiffJ::TypesComparator::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE
    make_fdiff_change msg, from_start, from_end, to_start, to_end
  end

  def test_changed
    run_test 'Changed', type_cls_to_iface(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
  end
end
