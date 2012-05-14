#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/tc'

include Java

module DiffJ::Type::Implements
  class TestCase < DiffJ::Type::TestCase
    def added_msg_fmt
      DiffJ::TypeComparator::IMPLEMENTED_TYPE_ADDED
    end  

    def changed_msg_fmt
      DiffJ::TypeComparator::IMPLEMENTED_TYPE_CHANGED
    end

    def removed_msg_fmt
      DiffJ::TypeComparator::IMPLEMENTED_TYPE_REMOVED
    end
  end
end
