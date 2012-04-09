#!/usr/bin/jruby -w
# -*- ruby -*-

require 'java'

class Java::net.sourceforge.pmd.ast::SimpleNode
  def get_children_serially children = java.util.ArrayList.new
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
        children.add t
      end
      n.get_children_serially children

      t = n.last_token
    end

    while t != get_last_token
      t = t.next
      children.add t
    end
    children
  end

  def parent
    jjt_get_parent
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
    list = java.util.ArrayList.new
    return list if jjt_get_num_children == 0
    
    n = jjt_get_child 0

    t = Java::net.sourceforge.pmd.ast.Token.new
    t.next = first_token
            
    while true
      t = t.next
      if t == n.first_token
        break
      else
        list.add t 
      end
    end

    list
  end

  def find_child clsname = nil, index = 0
    return nil if index && index < 0

    n_children = jjt_get_num_children
    return nil if index >= n_children

    n_found = -1
    (0 ... n_children).each do |idx|
      if child = get_child_of_type(clsname, idx)
        n_found += 1
        return child if n_found == index
      end
    end
          
    nil
  end

  def get_child_of_type clsname, idx
    child = jjt_get_child idx
    clsname.nil? || child.getClass().getName() == clsname ? child : nil
  end

  def snatch_children clsname
    list = Array.new
    n_children = jjt_get_num_children
    (0 ... n_children).each do |idx|
      child = jjt_get_child idx
      if clsname.nil? || child.getClass().getName() == clsname
        list << child
      end
    end
    list
  end

  def children get_nodes = true, get_tokens = true
    list = java.util.ArrayList.new
        
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
          list.add t
        end
      end
      if get_nodes
        list.add n
      end
      t = n.last_token
    end

    while t != last_token
      t = t.next
      if get_tokens
        list.add t
      end
    end

    list
  end

  def find_token token_type
    child_tokens = children false, true
    child_tokens.each do |tk|
      return tk if tk.kind == token_type
    end
    nil
  end
end
