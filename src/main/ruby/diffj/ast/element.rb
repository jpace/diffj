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

      args_for_conversion = Array.new
      msg = nil
      params = nil

      arglist = args.dup
      arglist.each_with_index do |arg, idx|
        info "arg: #{arg}".on_green
        info "arg.class: #{arg.class}".on_green
        info "idx: #{idx}".on_green

        if arg.kind_of? Java::net.sourceforge.pmd.ast.Token
          info "token"
          args_for_conversion << { :token => arg }
        elsif arg.kind_of? Java::net.sourceforge.pmd.ast.SimpleNode
          info "simple node"
          args_for_conversion << { :simple_node => arg }
        else
          info "neither"
          if msg
            (params ||= Array.new) << arg
            info "params: #{params}".yellow
          else
            msg = arg
          end
          # args_for_conversion << [ :param, arg ]
        end
        info "args_for_conversion: #{args_for_conversion}"
        info "msg: #{msg}"
        info "params: #{params}"
      end

      params ||= get_parameters args_for_conversion
      info "params: #{params}".bold

      return if true

      @tokens = conv_args[:tokens]
      info "tokens: #{@tokens.inspect}".bold

      params = conv_args[:params].empty? || get_parameters(conv_args)
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

    def get_message_and_params args
      msg = args.shift
      params = []
      while args[0].class == String || args[0].class == Fixnum
        arg = args.shift
        params << arg
      end

      [ msg, params ]
    end

    def get_tokens args
      tokens = Array.new

      simple_node_from = nil
      simple_node_to = nil

      while args.size > 0
        arg = args[0]
        if arg.class == String || arg.class == Fixnum
          break
        else
          arg = args.shift
          if arg.java_class.to_s == "net.sourceforge.pmd.ast.Token"
            tokens << arg
          else
            info "arg: #{arg}"
            # simple_node = arg.java_class.is_a? ::Java::net.sourceforge.pmd.ast.SimpleNode
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
      end

      if tokens.size == 3
        tokens.insert 0, tokens[0]
      end

      tokens
    end

    def convert_arguments args
      info "args: #{args.inspect}".red

      # should be 2, for from and to, or 4 for from_start, from_end, to_start, to_end
      args = args.dup

      tokens = get_tokens args
      info "tokens: #{tokens}".yellow
      
      msg, params = get_message_and_params args
      info "msg: #{msg}".yellow
      info "params: #{params}".yellow

      info "tokens: #{tokens}".on_blue
      info "msg: #{msg}".on_blue
      info "params: #{params}".on_blue

      simple_node_from = nil
      simple_node_to = nil

      { :simple_nodes => { :from => simple_node_from, :to => simple_node_to },
        :msg => msg,
        :params => params,
        :tokens => tokens
      }
    end

    def convert_arguments_orig args
      info "args: #{args.inspect}".red

      # should be 2, for from and to, or 4 for from_start, from_end, to_start, to_end
      tokens = Array.new
      msg = nil
      params = nil

      simple_node_from = nil
      simple_node_to = nil

      args.each_with_index do |arg, idx|
        info "arg: #{arg}".red
        info "arg.class: #{arg.class}".red, 10

        if arg.class == String || arg.class == Fixnum
          if msg
            (params ||= Array.new) << arg
          else
            msg = arg
          end
        elsif arg.java_class.to_s == "net.sourceforge.pmd.ast.Token"
          tokens << arg
          if idx == 0
            tokens << arg
          end
        else
          info "arg: #{arg}"
          # simple_node = arg.java_class.is_a? ::Java::net.sourceforge.pmd.ast.SimpleNode
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
    def initialize args
      info "args: #{args.class}".on_red
      info "args: #{args.inspect}".on_red

      arglist = args.dup

      tokens = Hash.new
      simple_nodes = Hash.new

      ast_elements = Array.new

      @tokens = Array.new
      @msg = nil
      @params = []

      arglist.each_with_index do |arg, idx|
        info "arg: #{arg}"
        if arg.class == String
          @msg = arg
          @params = arglist[idx + 1 .. -1]
          ast_elements = arglist[0 ... idx]
          break
        end
      end

      info "@tokens: #{@tokens}".yellow
      info "@msg: #{@msg}".yellow
      info "@params: #{@params}".yellow
      info "ast_elements: #{ast_elements}".yellow

      if ast_elements.size == 2
        ast_elements.each_with_index do |elmt, idx|
          if elmt.kind_of? Java::net.sourceforge.pmd.ast.Token
            @tokens << elmt
            if idx == 1
              @params << elmt.image
            end
          else
            @tokens << elmt.getFirstToken()
            @tokens << elmt.getLastToken()
            if idx == 1
              @params << SimpleNodeUtil.toString(elmt)
            end
          end
        end
      end

      info "@tokens: #{@tokens}".yellow
      info "@params: #{@params}".yellow

      str = MessageFormat.format @msg, *(@params)
      info "str: #{str}".yellow

      fdcls = get_filediff_cls
      
      if @tokens.length == 2
        @filediff = fdcls.new str, @tokens[0], @tokens[1]
      else
        @filediff = fdcls.new str, @tokens[0], @tokens[1], @tokens[2], @tokens[3]
      end
    end

    def get_parameters args_for_conversion
      params = Array.new
      case args_for_conversion.size
      when 4
        info "all tokens".red
      when 2
        info "two".red
        if sn = args_for_conversion[1][:simple_node]
          info "sn: #{sn}".on_blue
          snstr = SimpleNodeUtil.toString(sn)
          info "snstr: #{snstr}".on_blue
          params << snstr
        end
      end
      params
    end

    def get_parameters_orig conv_args
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

    def add ref
      @filediffs << ref
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
