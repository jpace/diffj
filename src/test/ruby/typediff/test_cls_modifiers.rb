#!/usr/bin/jruby -w
# -*- ruby -*-

require 'typediff/typetestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJTypeClsModifiersTestCase < DiffJTypeTestCase
  def subdir
    'clsmodifiers'
  end

  def added_msg_fmt
    ItemDiff::MODIFIER_ADDED
  end  

  def removed_msg_fmt
    ItemDiff::MODIFIER_REMOVED
  end

  def test_added
    run_test 'ClsModifierAdded', added("abstract", loc(1, 1), loc(1, 6), loc(1, 8))
  end

  def test_removed
    run_test 'ClsModifierRemoved', removed("strictfp", loc(1, 8), loc(1, 1), loc(1, 6))
  end
end
