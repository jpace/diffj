#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

module DiffJ
  module AST
    module Function
      def to_full_name tk, params
        types = params.get_parameter_types
        ary = Array.new
        types.each do |type|
          ary << type
        end
        args  = ary.join ", "
        tk.image + "(" + args + ")"
      end
      
      def throws_list
        it = children.iterator
        while it.hasNext
          obj = it.next
          if obj.kind_of?(Java::net.sourceforge.pmd.ast.Token) && obj.kind == Java::net.sourceforge.pmd.ast.JavaParserConstants::THROWS && it.hasNext
            return it.next
          end
        end
        nil
      end
    end
  end
end
