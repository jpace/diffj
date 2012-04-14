#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/body/codechange/tc'

module DiffJ::Type::Method::Body::CodeChange
  class ReturnValueTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_changed
      run_test 'Changed', changed("changed()", loc(20, 1), loc(20, 1), loc(24, 44), loc(24, 44))
    end
  end
end
