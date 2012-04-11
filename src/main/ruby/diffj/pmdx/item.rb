#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

module DiffJ
  module AST
    module Item
      ACCESSES = [ ::Java::net.sourceforge.pmd.ast.JavaParserConstants::PUBLIC,
                   ::Java::net.sourceforge.pmd.ast.JavaParserConstants::PROTECTED,
                   ::Java::net.sourceforge.pmd.ast.JavaParserConstants::PRIVATE ]
      
      def accesstk
        ACCESSES.each do |acc|
          if tk = leading_token(:token_type => acc)
            return tk
          end
        end
        nil
      end
      
      def to_full_name tk, params
        types = params.get_parameter_types
        tk.image + "(" + types.join(", ") + ")"
      end

      def is_throws_token? obj
        obj.kind_of?(Java::net.sourceforge.pmd.ast.Token) && obj.kind == Java::net.sourceforge.pmd.ast.JavaParserConstants::THROWS
      end
      
      def throws_list
        kids = all_children
        kids.each_with_index do |child, idx|
          return kids[idx + 1] if is_throws_token?(child) && idx + 1 < kids.size
        end
        nil
      end
    end
  end
end
