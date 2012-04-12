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
      super from.parameters, to.parameters
    end

    def compare_throws from, to
      super from, from.throws_list, to, to.throws_list
    end
    
    def get_code ctor
      # removes all tokens up to the first left brace. This is because ctors
      # don't have their own blocks, unlike methods.
        
      code = ctor.get_child_tokens
      first_lbrace = code.index { |tk| tk.kind == ::Java::net.sourceforge.pmd.ast.JavaParserConstants::LBRACE }

      code[first_lbrace .. -1]
    end

    def compare_bodies from, to
      from_code = get_code from
      to_code = get_code to
        
      compare_code from.fullname, from_code, to.fullname, to_code
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
