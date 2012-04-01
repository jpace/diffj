#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJ::TypeClsModifiersTestCase < DiffJTypeTestCase
  def subdir
    'modifiers'
  end

  def added_msg_fmt
    ItemDiff::MODIFIER_ADDED
  end  

  def removed_msg_fmt
    ItemDiff::MODIFIER_REMOVED
  end

  def test_added
    run_test 'Added', added('abstract', loc(1, 1), loc(1, 6), loc(1, 8))
  end

  def test_removed
    run_test 'Removed', removed('strictfp', loc(1, 8), loc(1, 1), loc(1, 6))
  end
end
