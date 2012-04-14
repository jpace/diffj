#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/diffjtestcase'

include Java

class DiffJTypeTestCase < DiffJ::TestCase
  BASEDIR = 'type'

  def run_test basename, *expected_fdiffs
    run_fdiff_test expected_fdiffs, BASEDIR + '/' + subdir, basename
  end
end
