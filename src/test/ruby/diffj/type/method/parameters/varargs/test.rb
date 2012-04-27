#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class VarargsTestCase < TestCase
    def changed_msg_fmt
      DiffJ::FunctionComparator::PARAMETER_NAME_CHANGED
    end  

    def test_unchanged
      # $$$ this is disabled
      # run_test 'Unchanged'
    end

    def test_changed
      # $$$ this is disabled
      # run_test 'Changed', changed("idx", "index", loc(2, 22), loc(2, 22))
    end
  end
end
