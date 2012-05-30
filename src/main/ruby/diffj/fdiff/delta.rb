#!/usr/bin/jruby -w
# -*- ruby -*-

require 'riel/log'
require 'java'
require 'diffj/fdiff/fdiff'
require 'diffj/util/resstring'

include Java

module DiffJ
  class Delta
    include Loggable
    
    attr_reader :filediff

    def initialize args
      ast_elements = Array.new

      @tokens = Array.new
      @msg = nil
      @params = nil

      args.each_with_index do |arg, idx|
        if arg.class == String
          @msg = arg
          @params = args[idx + 1 .. -1]
          ast_elements = args[0 ... idx]
          break
        end
      end

      if ast_elements.size == 4
        @tokens = ast_elements
      else
        ast_classes = ast_elements.collect { |ast| ast.class.to_s.sub(%r{.*::}, '').sub(%r{AST\w+}, 'SimpleNode').downcase }.join('_')
        meth = "process_#{ast_classes}".to_sym
        method(meth).call(*ast_elements)
      end

      str = ResourceString.new(@msg).format(*@params)
      fdcls = get_filediff_cls

      @filediff = fdcls.new str, :tokens => @tokens
    end    

    def process_token_simplenode from_tk, to_sn
      @tokens.concat [ from_tk, from_tk, to_sn.token(0), to_sn.token(-1) ]
    end

    def process_simplenode_token from_sn, to_tk
      @tokens.concat [ from_sn.token(0), from_sn.token(-1), to_tk, to_tk  ]
    end

    def process_token_token from_tk, to_tk
      @tokens.concat [ from_tk, to_tk ]
      if @params.empty?
        @params = tokens_to_parameters from_tk, to_tk
      end      
    end

    def process_simplenode_simplenode from_sn, to_sn
      @tokens.concat [ from_sn.token(0), from_sn.token(-1), to_sn.token(0), to_sn.token(-1) ]
      if @params.empty?
        @params = nodes_to_parameters from_sn, to_sn
      end
    end

    def get_filediff_class
    end

    def tokens_to_parameters from, to
      [ from, to ].compact.collect { |p| p.image }
    end

    def nodes_to_parameters from, to
      [ from, to ].compact.collect { |p| p.to_string }
    end
  end

  class Add < Delta
    def tokens_to_parameters from_tk, to_tk
      super nil, to
    end

    def nodes_to_parameters from_sn, to_sn
      super nil, to_sn
    end

    def get_filediff_cls
      DiffJ::FDiffAdd
    end
  end

  class Remove < Delta    
    def tokens_to_parameters from_tk, to_tk
      super from_tk, nil
    end

    def nodes_to_parameters from_sn, to_sn
      super from_sn, nil
    end

    def get_filediff_cls
      DiffJ::FDiffDelete
    end
  end

  class Change < Delta
    def get_filediff_cls
      DiffJ::FDiffChange
    end
  end
end
