#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'
require 'diffj/ast/type'

include Java

class DiffJ::TypeImplementsTestCase < DiffJTypeTestCase
  def subdir
    # info "self.class: #{self.class}"
    # self.class.to_s.sub(%r{.*::(\w+)TestCase}, '\1').downcase
    'implements'
  end
  
  def added_msg_fmt
    DiffJ::TypeComparator::IMPLEMENTED_TYPE_ADDED
  end  

  def changed_msg_fmt
    DiffJ::TypeComparator::IMPLEMENTED_TYPE_CHANGED
  end

  def removed_msg_fmt
    DiffJ::TypeComparator::IMPLEMENTED_TYPE_REMOVED
  end

  def test_added
    run_test 'Added', added_change("java.util.List", loc(1, 8), loc(2, 1), loc(1, 31))
  end

  def test_removed
    run_test 'Removed', removed_change("Runnable", loc(1, 33), loc(1, 8), loc(2, 1))
  end

  def test_changed
    run_test 'Changed', changed("java.io.DataOutput", "java.io.DataInput", loc(1, 33), loc(1, 33))
  end
end
