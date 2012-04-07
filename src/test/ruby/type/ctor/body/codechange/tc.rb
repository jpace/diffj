#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/ctor/body/tc'
require 'diffj/ast/item'

include Java

module DiffJ::Type::Ctor::Body::CodeChange
  class TestCase < DiffJ::Type::Ctor::Body::TestCase
    def added_msg_fmt
      DiffJ::ItemComparator::CODE_ADDED
    end  

    def changed_msg_fmt
      DiffJ::ItemComparator::CODE_CHANGED
    end  

    def removed_msg_fmt
      DiffJ::ItemComparator::CODE_REMOVED
    end
  end
end
