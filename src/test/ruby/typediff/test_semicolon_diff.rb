#!/usr/bin/jruby -w
# -*- ruby -*-

require 'typediff/typetestcase'

include Java

import org.incava.diffj.ItemDiff

class DiffJTypeSemicolonTestCase < DiffJTypeTestCase
  def subdir
    'semicolon'
  end

  def test_none
    run_test 'None'
  end
end
