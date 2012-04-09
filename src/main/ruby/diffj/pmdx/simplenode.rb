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
      while true
        t = t.next
        if t == n.first_token
          break
        end
        children << t
      end
      n.get_children_serially children
      t = n.last_token
    end

    while t != get_last_token
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
    
    n = jjt_get_child 0
    
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
            
    while true
      t = t.next
      if t == n.first_token
        return ary
      else
        ary << t 
      end
    end
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

  def children get_nodes = true, get_tokens = true
    ary = Array.new
    
    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
    
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      n = jjt_get_child idx
      while true
        t = t.next
        if t == n.first_token
          break
        end
        if get_tokens
          ary << t
        end
      end
      if get_nodes
        ary << n
      end
      t = n.last_token
    end

    while t != last_token
      t = t.next
      if get_tokens
        ary << t
      end
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

  def find_token token_type
    child_tokens = children false, true
    child_tokens.each do |tk|
      return tk if tk.kind == token_type
    end
    nil
  end
end
