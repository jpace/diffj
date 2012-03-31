#!/usr/bin/jruby -w
# -*- ruby -*-

require 'typediff/typetestcase'

include Java

import org.incava.diffj.TypeDiff

class DiffJTypeExtendsTestCase < DiffJTypeTestCase
  def subdir
    'extends'
  end

  def added_msg_fmt
    TypeDiff::EXTENDED_TYPE_ADDED
  end  

  def changed_msg_fmt
    TypeDiff::EXTENDED_TYPE_CHANGED
  end

  def removed_msg_fmt
    TypeDiff::EXTENDED_TYPE_REMOVED
  end

  def test_added
    run_test 'Added', added("java.io.File", loc(1, 8), loc(2, 1), loc(1, 28))
  end

  def test_removed
    run_test 'Removed', removed("java.lang.StringBuilder", loc(1, 30), loc(1, 8), loc(2, 1))
  end

  def test_changed
    run_test 'Changed', changed("java.text.DateFormat", "java.text.MessageFormat", loc(1, 30), loc(1, 30))
  end
end
