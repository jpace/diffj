#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/ctor/throws/tc'

module DiffJ::Type::Ctor::Throws
  class ZeroOneTestCase < TestCase
    def test_added
      run_test 'Added', added_change("Exception", loc(2, 5), loc(3, 5), loc(2, 20), loc(2, 28))
    end

    def test_removed
      run_test 'Removed', removed_change("org.xml.sax.SAXParseException", loc(2, 22), loc(2, 5), loc(3, 5))
    end
  end
end
