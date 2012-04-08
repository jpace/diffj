#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'
require 'diffj/ast/type'

include Java

module DiffJ::Type::Declarations::InnerType
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      DiffJ::TypeComparator::CONSTRUCTOR_ADDED
    end  

    def changed_msg_fmt
      raise "not implemented"
    end

    def removed_msg_fmt
      DiffJ::TypeComparator::CONSTRUCTOR_REMOVED
    end
  end
end
