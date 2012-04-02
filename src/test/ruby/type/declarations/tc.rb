#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/tc'

include Java

module DiffJ::Type::Declarations
  class TestCase < DiffJ::Type::TestCase
  end
end
