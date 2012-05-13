#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'
require 'diffj/pmdx/item'

# $$$
# sn = ASTXxxYyy
# sn.size == jjt_get_num_children
# sn[3] == jjt_get_child 3
# sn[-1] == jjt_get_child(jjt_get_num_children - 1)

# sn.node(n) == sn[n]

# sn.find_node(:class => clsname)
# sn.find_nodes(:class => clsname)
# sn.find_nodes(:class => clsname)

# sn.token(n) == sn[n]

# sn.find_tokens(:kind => token_type)
# sn.find_token(:kind => token_type, :nth => 2)

class Java::net.sourceforge.pmd.ast::SimpleNode
  include Loggable, DiffJ::AST::Item

  def tokens 
    get_token_range
  end

  alias_method :get_child_tokens, :tokens
  
  def parent
    jjt_get_parent
  end

  def [] idx
    node idx
  end

  def node idx
    idx < size && jjt_get_child(idx)
  end

  def size
    jjt_get_num_children
  end

  alias_method :length, :size

  def token idx
    idx == 0 ? first_token : (idx == -1 ? last_token : tokens[idx])
  end

  def to_string
    tokens.collect { |tk| tk.image }.join ''
  end

  def leading_tokens
    return Array.new if jjt_get_num_children == 0
    get_token_range first_token, node(0).token(0)
  end

  # returns the tokens from from to to inclusive.
  def get_token_range from = first_token, to = last_token
    tokens = Array.new
    tk = from
    while true
      tokens << tk
      break if tk.object_id == to.object_id
      tk = tk.next
    end
    tokens
  end

  def matches_class? node, clsname
    (clsname.nil? || node.getClass().getName() == clsname) && node
  end

  # returns the nth child matching the given class name
  def find_child clsname = nil, n = 0
    return nil if n && n < 0
    nfound = -1
    nodes.detect do |child|
      matches_class?(child, clsname) && (nfound += 1) == n
    end
  end

  def find_children clsname
    nodes.select do |child|
      matches_class? child, clsname
    end
  end

  # returns the first token matching the criteria
  def leading_token criteria
    return nil if length == 0
    
    # stop at the first token of a subnode:
    ntk = self[0].token(0)
    
    tk = token(0)
    while tk && ntk != tk
      return tk if tk.kind == criteria[:token_type]
      tk = tk.next
    end
    nil
  end

  def all_children
    # as confusing as this implementation is, it's clearer than any alternatives.
    list = Array.new
    
    tk = Java::net.sourceforge.pmd.ast::Token.new
    tk.next = first_token
        
    (0 ... size).each do |nidx|
      subnode = node(nidx)
      while true
        tk = tk.next
        if tk == subnode.first_token
          break
        end
        list << tk
      end
      list << subnode
      tk = subnode.last_token
    end
    
    while tk != last_token
      tk = tk.next
      list << tk
    end
    list
  end

  def nodes
    ary = Array.new
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      ary << jjt_get_child(idx)
    end
    ary
  end    

  def find_token token_type
    tk = first_token
    lasttk = last_token
    while tk
      return tk if tk.kind == token_type
      tk = tk.next
    end
    nil
  end
  
  def dump_node indent = "", recurse = true
    info "#{indent}#{inspect}"
    
    tk = get_first_token
    ltk = get_last_token

    while tk != ltk
      info "#{indent}tk: #{tk.inspect}"
      info "#{indent}tk: #{tk.kind}; #{tk.image}"
      tk = tk.next
    end

    info "#{indent}tk: #{tk.inspect}"
    info "#{indent}tk: #{tk.kind}; #{tk.image}"

    # ldg = leading_tokens 
    # info "ldg: #{ldg}"

    n_children = jjt_get_num_children
    info "#{indent}n_children: #{n_children}"

    (0 ... n_children).each do |ci|
      child = jjt_get_child ci
      info "#{indent}child: #{child}"

      if recurse
        child.dump_node indent + "    ", recurse
      end
    end

    tokens.each do |tk|
      info "#{indent}#{tk}"
    end
  end

end
