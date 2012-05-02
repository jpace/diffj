#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/body/codechange/tc'

module DiffJ::Type::Method::Body::CodeChange
  class StatementTestCase < TestCase
    def test_unchanged
      run_test 'Unchanged'
    end

    def test_changed
      run_test 'Changed', changed("meth(String, char)", loc(3, 42), loc(3, 81), loc(3, 37), loc(3, 58))
    end
  end
end
