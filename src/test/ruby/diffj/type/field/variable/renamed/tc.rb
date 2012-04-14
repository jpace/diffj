#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/field/variable/tc'
require 'diffj/ast/field'

include Java

module DiffJ::Type::Field::Variable::Renamed
  class TestCase < DiffJ::Type::Field::Variable::TestCase
    def changed_msg_fmt
      DiffJ::FieldComparator::VARIABLE_CHANGED
    end    
  end
end
