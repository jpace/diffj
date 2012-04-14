#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/tc'

include Java

module DiffJ::Type::Declarations
  class TestCase < DiffJ::Type::TestCase
  end
end
