#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/typetestcase'
require 'diffj/ast/item'

include Java

class DiffJ::TypeAccessTestCase < DiffJTypeTestCase
  def subdir
    'access'
  end

  def added_msg_fmt
    DiffJ::ItemComparator::ACCESS_ADDED
  end  

  def changed_msg_fmt
    DiffJ::ItemComparator::ACCESS_CHANGED
  end  

  def removed_msg_fmt
    DiffJ::ItemComparator::ACCESS_REMOVED
  end

  def test_removed
    run_test 'Removed', removed_change("public", loc(1, 1), loc(1, 1), loc(1, 5))
  end

  def test_added
    run_test 'Added', added_change("public", loc(1, 1), loc(1, 5), loc(1, 1))
  end

  def test_changed
    run_test 'Changed', changed("public", "protected", loc(1, 1), loc(1, 1))
  end
end
