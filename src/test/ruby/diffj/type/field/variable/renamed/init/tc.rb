#!/usr/bin/jruby -w
# -*- ruby -*-

require 'diffj/type/field/variable/renamed/tc'
require 'diffj/ast/field'

include Java

module DiffJ::Type::Field::Variable::Renamed::Init
  class TestCase < DiffJ::Type::Field::Variable::Renamed::TestCase
    def changed_msg_fmt
      DiffJ::FieldComparator::VARIABLE_CHANGED
    end    
  end
end
