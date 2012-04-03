#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import java.text.MessageFormat
import org.incava.analysis.FileDiffAdd
import org.incava.analysis.FileDiffChange
import org.incava.analysis.FileDiffDelete
import org.incava.pmdx.SimpleNodeUtil

module DiffJ
  class Delta
    include Loggable
    
    attr_reader :filediff

    def initialize args
      stack "args: #{args.class}".on_red
      info "args: #{args.inspect}".on_red

      conv_args = convert_arguments args
      info "conv_args: #{conv_args}".bold

      @tokens = conv_args[:tokens]

      params = conv_args[:params] || get_parameters(conv_args)
      msg = conv_args[:msg]
      str = MessageFormat.format msg, *params

      info "str: #{str}".on_blue

      fdcls = get_filediff_cls
      
      if @tokens.length == 2
        @filediff = fdcls.new str, @tokens[0], @tokens[1]
      else
        @filediff = fdcls.new str, @tokens[0], @tokens[1], @tokens[2], @tokens[3]
      end
    end

    def convert_arguments args
      # should be 2, for from and to, or 4 for from_start, from_end, to_start, to_end
      tokens = Array.new
      msg = nil
      params = nil

      simple_node_from = nil
      simple_node_to = nil

      args.each do |arg|
        info "arg: #{arg}".red
        info "arg.class: #{arg.class}".red

        if arg.class == String
          if msg
            (params ||= Array.new) << arg
          else
            msg = arg
          end
        elsif arg.java_class.to_s == "net.sourceforge.pmd.ast.Token"
          tokens << arg
        else
          info "arg: #{arg}"
          simple_node = arg.java_class.is_a? ::Java::net.sourceforge.pmd.ast.SimpleNode
          simple_node = arg.is_a? ::Java::net.sourceforge.pmd.ast.SimpleNode
          if simple_node
            firsttoken = arg.getFirstToken()
            info "firsttoken: #{firsttoken}".on_magenta

            tokens << firsttoken

            lasttoken = arg.getLastToken()
            info "lasttoken: #{lasttoken}".on_magenta

            tokens << lasttoken
            
            if simple_node_from
              simple_node_to = arg              
            else
              simple_node_from = arg
            end
          end
        end
      end

      info "tokens: #{tokens}".on_blue
      info "msg: #{msg}".on_blue
      info "params: #{params}".on_blue

      { :simple_nodes => { :from => simple_node_from, :to => simple_node_to },
        :msg => msg,
        :params => params,
        :tokens => tokens
      }
    end

    def get_parameters
    end

    def get_filediff_class
    end

    def tokens_to_parameters from, to
      params = java.util.ArrayList.new
      if from
        params.add(from.image)
      end
      if to
        params.add(to.image)
      end
      params.toArray
    end

    def nodes_to_parameters from, to
      params = java.util.ArrayList.new
      if from
        params.add(SimpleNodeUtil.toString(from))
      end
      if to
        params.add(SimpleNodeUtil.toString(to))
      end
      params.toArray
    end
  end

  class Add < Delta
    def get_parameters conv_args
      if sn = conv_args[:simple_nodes][:to]
        nodes_to_parameters nil, sn
      else
        tokens_to_parameters nil, @tokens[-1]
      end      
    end

    def get_filediff_cls
      FileDiffAdd
    end
  end

  class Remove < Delta
    def get_parameters conv_args
      if sn = conv_args[:simple_nodes][:from]
        nodes_to_parameters sn, nil
      else
        tokens_to_parameters @tokens[0], nil
      end      
    end

    def get_filediff_cls
      FileDiffDelete
    end
  end

  class Change < Delta
    def get_parameters conv_args
      if snfrom = conv_args[:simple_nodes][:from]
        snto = conv_args[:simple_nodes][:to]
        nodes_to_parameters snfrom, snto
      else
        tokens_to_parameters @tokens[0], @tokens[-1]
      end      
    end

    def get_filediff_cls
      FileDiffChange
    end
  end

  class ElementComparator
    include Loggable

    attr_reader :filediffs

    def initialize filediffs
      @filediffs = filediffs
    end

    def changed *args
      chgobj = Change.new args
      @filediffs << chgobj.filediff
    end

    def added *args
      addobj = Add.new args
      @filediffs << addobj.filediff
    end

    def deleted *args
      remobj = Remove.new args
      @filediffs << remobj.filediff
    end
  end    
end
