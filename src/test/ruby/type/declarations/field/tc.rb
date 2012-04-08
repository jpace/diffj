#!/usr/bin/jruby -w
# -*- ruby -*-

require 'type/declarations/tc'
require 'diffj/ast/type'

include Java

module DiffJ::Type::Declarations::Field
  class TestCase < DiffJ::Type::Declarations::TestCase
    def added_msg_fmt
      DiffJ::TypeComparator::FIELD_ADDED
    end  

    def changed_msg_fmt
      DiffJ::TypeComparator::FIELD_CHANGED    # not implemented ?
    end

    def removed_msg_fmt
      DiffJ::TypeComparator::FIELD_REMOVED
    end
  end
end
