#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/tc'

include Java

module DiffJ::Type
  class SemicolonTestCase < TestCase
    def test_none
      run_test 'None'
    end
  end
end
