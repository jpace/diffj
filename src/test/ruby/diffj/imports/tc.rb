#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/tc'
require 'diffj/ast/imports'

include Java

module DiffJ::Imports
  class TestCase < DiffJ::TestCase
    def subdir
      self.class.to_s.sub(%r{TestCase$}, '').downcase.split("::")[1 .. -1].join('/')
    end

    def run_imp_test basename, *expected_fdiffs
      run_fdiff_test expected_fdiffs, subdir, basename
    end
  end
end
