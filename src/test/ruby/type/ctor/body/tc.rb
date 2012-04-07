#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/ctor/tc'
require 'diffj/ast/method'

include Java

module DiffJ::Type::Ctor::Body
  class TestCase < DiffJ::Type::Ctor::TestCase
  end
end
