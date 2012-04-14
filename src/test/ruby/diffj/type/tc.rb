#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'

include Java

module DiffJ::Type
  class TestCase < DiffJ::TestCase
    BASEDIR = 'type'

    def subdir
      self.class.to_s.sub(%r{TestCase$}, '').downcase.split("::")[1 .. -1].join('/')
    end
    
    def run_test basename, *expected_fdiffs
      run_fdiff_test expected_fdiffs, subdir, basename
    end
  end
end
