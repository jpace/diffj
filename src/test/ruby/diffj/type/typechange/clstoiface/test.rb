#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/typechange/tc'
require 'diffj/ast/types'

include Java

module DiffJ::Type::TypeChange
  class ClsToIfaceTestCase < TestCase
    def changed_msg_fmt
      DiffJ::TypesComparator::TYPE_CHANGED_FROM_CLASS_TO_INTERFACE
    end  

    def test_changed
      run_test 'Changed', changed(loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1))
    end
  end
end
