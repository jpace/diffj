#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::SimpleNode
  def get_children_serially children = Array.new
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
        
    n_children = jjt_get_num_children
    (0 ... n_children).each do |ord|
      n = jjt_get_child ord
      ntk = n.first_token
      while true
        t = t.next
        if t == ntk
          break
        end
        children << t
      end
      n.get_children_serially children
      t = n.last_token
    end

    lasttk = token(-1)
    while t != lasttk
      t = t.next
      children << t
    end
    children
  end

  def parent
    jjt_get_parent
  end

  def [] idx
    jjt_get_child idx
  end

  def size
    jjt_get_num_children
  end

  alias_method :length, :size

  def token idx
    idx == 0 ? first_token : (idx == -1 ? last_token : raise("not handled: token[#{idx}]"))
  end

  def to_string
    tk = first_token
    last = last_token
    str = tk.image
    while tk != last
      tk = tk.next
      str << tk.image
    end
    str
  end

  def leading_tokens
    ary = Array.new
    return ary if jjt_get_num_children == 0
    
    n = self[0]
    
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token

    return preceding_tokens(first_token, n)
  end

  def matches_class? node, clsname
    (clsname.nil? || node.getClass().getName() == clsname) && node
  end

  # returns the nth child matching the given class name
  def find_child clsname = nil, n = 0
    return nil if n && n < 0
    nfound = -1
    childnodes.detect do |child|
      matches_class?(child, clsname) && (nfound += 1) == n
    end
  end

  def get_child_of_type clsname, idx
    child = jjt_get_child idx
    matches_class? child, clsname
  end

  def find_children clsname
    childnodes.select do |child|
      matches_class? child, clsname
    end
  end

  def preceding_tokens fromtk, node = self
    ary = Array.new
    ntk = node.token(0)

    tk = fromtk

    while tk != ntk
      ary << tk
      tk = tk.next
    end
    ary
  end

  # def trailing_tokens fromtk, node = self
  #   lasttk = node[-1]
  #   tk = fromtk
  #   while tk != lasttk
  #     ary << t
  #     t = t.next
  #   end
  # end

  def all_children
    ary = Array.new
    
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
    
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      n = jjt_get_child idx
      ntk = n.token(0)
      while true
        t = t.next
        if t == ntk
          break
        end
        ary << t
      end
      ary << n
      t = n.token(-1)
    end

    while t != last_token
      t = t.next
      ary << t
    end

    ary
  end

  def childnodes
    ary = Array.new
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      ary << jjt_get_child(idx)
    end
    ary
  end    

  def childtokens
    ary = Array.new
    
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
    
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      n = jjt_get_child idx
      # ary.concat preceding_tokens(
      ntk = n.token(0)
      while true
        t = t.next
        if t == ntk
          break
        end
        ary << t
      end
      t = n.token(-1)
    end

    while t != last_token
      t = t.next
      ary << t
    end

    ary
  end

  def find_token token_type
    childtokens.each do |tk|
      return tk if tk.kind == token_type
    end
    nil
  end
end
