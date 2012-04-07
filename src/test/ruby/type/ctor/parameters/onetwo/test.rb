#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/ctor/parameters/tc'

module DiffJ::Type::Ctor::Parameters
  class OneTwoTestCase < TestCase
    def test_added
      run_test 'Added', added_change("width", loc(2, 10), loc(2, 24), loc(2, 26), loc(2, 34))
    end

    def test_removed
      run_test 'Removed', removed_change("stream", loc(2, 23), loc(2, 48), loc(2, 12), loc(2, 21))
    end
  end
end
