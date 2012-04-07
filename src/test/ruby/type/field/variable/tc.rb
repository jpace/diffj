#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/field/tc'

include Java

module DiffJ::Type::Field::Variable
  class TestCase < DiffJ::Type::Field::TestCase
  end
end
