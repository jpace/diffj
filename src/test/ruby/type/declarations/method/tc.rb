#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'
require 'diffj/ast/type'

include Java

module DiffJ::Type::Declarations::Method
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      DiffJ::TypeComparator::METHOD_ADDED
    end  

    def changed_msg_fmt
      DiffJ::TypeComparator::METHOD_CHANGED    # not implemented
    end

    def removed_msg_fmt
      DiffJ::TypeComparator::METHOD_REMOVED
    end
  end
end
