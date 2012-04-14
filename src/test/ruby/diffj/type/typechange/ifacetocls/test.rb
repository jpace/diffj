#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/typechange/tc'
require 'diffj/ast/types'

include Java

module DiffJ::Type::TypeChange
  class IfaceToClsTestCase < TestCase
    def changed_msg_fmt
      DiffJ::TypesComparator::TYPE_CHANGED_FROM_INTERFACE_TO_CLASS
    end  

    def test_changed
      run_test 'Changed', changed(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
    end
  end
end
