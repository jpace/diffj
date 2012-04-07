#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'
require 'diffj/ast/function'

include Java

import org.incava.analysis.FileDiff
import org.incava.diffj.ItemDiff
import org.incava.ijdk.text.LocationRange
import org.incava.pmdx.CtorUtil
import org.incava.pmdx.ParameterUtil

module DiffJ
  class CtorComparator < FunctionComparator
    include Loggable

    def ctor_compare_parameters_xxx from, to
      from_formal_params = CtorUtil.getParameters from
      to_formal_params = CtorUtil.getParameters to
      
      function_compare_parameters_xxx from_formal_params, to_formal_params
    end

    def ctor_compare_throws_xxx from, to
      from_name_list = CtorUtil.getThrowsList from
      to_name_list = CtorUtil.getThrowsList to

      function_compare_throws_xxx from, from_name_list, to, to_name_list
    end
    
    def ctor_get_code_serially_xxx ctor
      # removes all tokens up to the first left brace. This is because ctors
      # don't have their own blocks, unlike methods.
        
      children = SimpleNodeUtil.getChildrenSerially ctor
        
      it = children.iterator();
      while it.hasNext()
        tk = it.next();
        if tk.kind == ::Java::net.sourceforge.pmd.ast.JavaParserConstants::LBRACE
          break
        else
          it.remove()
        end
      end
      children
    end

    def ctor_compare_bodies_xxx from, to
      from_code = ctor_get_code_serially_xxx from
      to_code = ctor_get_code_serially_xxx to
        
      from_name = CtorUtil.getFullName from
      to_name = CtorUtil.getFullName to

      compare_code from_name, from_code, to_name, to_code
    end

    def compare_xxx from, to
      info "from: #{from}".on_red
      info "to  : #{to}".on_red

      ctor_compare_parameters_xxx from, to
      ctor_compare_throws_xxx from, to
      ctor_compare_bodies_xxx from, to
    end
  end
end
