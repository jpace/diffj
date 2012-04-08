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
      from_formal_params = org.incava.pmdx.CtorUtil.getParameters from
      to_formal_params = org.incava.pmdx.CtorUtil.getParameters to
      
      super from_formal_params, to_formal_params
    end

    def compare_throws from, to
      from_name_list = org.incava.pmdx.CtorUtil.getThrowsList from
      to_name_list = org.incava.pmdx.CtorUtil.getThrowsList to

      super from, from_name_list, to, to_name_list
    end
    
    def get_code_serially ctor
      # removes all tokens up to the first left brace. This is because ctors
      # don't have their own blocks, unlike methods.
        
      children =  ctor.get_children_serially
        
      it = children.iterator
      while it.hasNext
        tk = it.next
        if tk.kind == ::Java::net.sourceforge.pmd.ast.JavaParserConstants::LBRACE
          break
        else
          it.remove
        end
      end
      children
    end

    def compare_bodies from, to
      from_code = get_code_serially from
      to_code = get_code_serially to
        
      from_name = org.incava.pmdx.CtorUtil.getFullName from
      to_name = org.incava.pmdx.CtorUtil.getFullName to

      compare_code from_name, from_code, to_name, to_code
    end

    def compare from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      compare_parameters from, to
      compare_throws from, to
      compare_bodies from, to
    end
  end
end
