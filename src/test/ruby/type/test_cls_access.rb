#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJTypeClsAccessTestCase < DiffJTypeTestCase
  def subdir
    'clsaccess'
  end

  def added_msg_fmt
    ItemDiff::ACCESS_ADDED
  end  

  def changed_msg_fmt
    ItemDiff::ACCESS_CHANGED
  end

  def removed_msg_fmt
    ItemDiff::ACCESS_REMOVED
  end

  def test_public_to_none
    run_test 'ClsAccessPublicToNone', removed("public", loc(1, 1), loc(1, 1), loc(1, 5))
  end

  def test_none_to_public
    run_test 'ClsAccessNoneToPublic', added("public", loc(1, 1), loc(1, 5), loc(1, 1))
  end

  def test_public_to_protected
    run_test 'ClsAccessPublicToProtected', changed("public", "protected", loc(1, 1), loc(1, 1))
  end
end
