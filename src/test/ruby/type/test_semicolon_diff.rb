#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/typetestcase'

include Java

class DiffJTypeSemicolonTestCase < DiffJTypeTestCase
  def subdir
    'semicolon'
  end

  def test_none
    run_test 'None'
  end
end
