#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/method/parameters/tc'

module DiffJ::Type::Method::Parameters
  class ManyTestCase < TestCase
    def test_unchanged
      start = Time.new
      run_test 'Unchanged'
      done = Time.new
      info "elapsed: #{done - start}"
    end
  end
end
