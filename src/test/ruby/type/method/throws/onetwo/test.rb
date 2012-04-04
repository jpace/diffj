#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/method/throws/tc'

module DiffJ::Type::Method::Throws
  class OneTwoTestCase < TestCase
    def test_added
      run_test 'Added', added_change("java.beans.InstrospectionException", loc(2, 25), loc(2, 44), loc(2, 47))
    end

    def test_removed
      run_test 'Removed', removed_change("java.rim.ServerRuntimeException", loc(2, 64), loc(2, 27), loc(2, 61))
    end
  end
end
