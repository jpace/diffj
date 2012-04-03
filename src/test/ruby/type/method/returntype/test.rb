#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/tc'

import org.incava.diffj.MethodDiff
import org.incava.diffj.ItemDiff

module DiffJ::Type::Method
  class ReturnTypeTestCase < TestCase
    def changed_msg_fmt
      MethodDiff::RETURN_TYPE_CHANGED
    end  

    def test_changed
      run_test 'Changed', changed("int", "double", loc(2, 5), loc(2, 5))
    end
  end
end
