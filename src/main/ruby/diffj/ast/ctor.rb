#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

include Java

module DiffJ
  class CtorComparator < FunctionComparator
    include Loggable

    def compare_parameters from, to
      from_formal_params = from.parameters
      to_formal_params = to.parameters
      
      super from_formal_params, to_formal_params
    end

    def compare_throws from, to
      from_name_list = from.throws_list
      to_name_list = to.throws_list

      super from, from_name_list, to, to_name_list
    end
    
    def get_code_serially ctor
      # removes all tokens up to the first left brace. This is because ctors
      # don't have their own blocks, unlike methods.
        
      code = ctor.get_children_serially
      first_lbrace = code.index { |tk| tk.kind == ::Java::net.sourceforge.pmd.ast.JavaParserConstants::LBRACE }

      code[first_lbrace .. -1]
    end

    def compare_bodies from, to
      from_code = get_code_serially from
      to_code = get_code_serially to
        
      from_name = from.fullname
      to_name = to.fullname

      compare_code from_name, from_code, to_name, to_code
    end

    def compare from, to
      info "from: #{from}"
      info "to  : #{to}"
      
      compare_parameters from, to
      compare_throws from, to
      compare_bodies from, to
    end
  end
end
