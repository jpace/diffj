#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import org.incava.pmdx.ThrowsUtil

module DiffJ
  module FunctionComparator # < FunctionDiff

    RETURN_TYPE_CHANGED = "return type changed from {0} to {1}"

    PARAMETER_REMOVED = "parameter removed: {0}"
    PARAMETER_ADDED = "parameter added: {0}"
    PARAMETER_REORDERED = "parameter {0} reordered from argument {1} to {2}"

    PARAMETER_TYPE_CHANGED = "parameter type changed from {0} to {1}"
    PARAMETER_NAME_CHANGED = "parameter name changed from {0} to {1}"
    PARAMETER_REORDERED_AND_RENAMED = "parameter {0} reordered from argument {1} to {2} and renamed {3}"

    THROWS_REMOVED = "throws removed: {0}"
    THROWS_ADDED = "throws added: {0}"
    THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}"
    
    def compare_return_types_xxx from, to
      from_ret_type     = from.jjtGetChild(0)
      to_ret_type       = to.jjtGetChild(0)
      from_ret_type_str = SimpleNodeUtil.toString from_ret_type
      to_ret_type_str   = SimpleNodeUtil.toString to_ret_type

      if from_ret_type_str != to_ret_type_str
        changed from_ret_type, to_ret_type, RETURN_TYPE_CHANGED, from_ret_type_str, to_ret_type_str
      end
    end

    def function_compare_each_parameter_xxx from_formal_params, from_params, to_formal_params, to_params, size
      (0 ... size).each do |idx|
        from_param = from_params.get idx
        param_match = ParameterUtil.getMatch from_params, idx, to_params

        from_formal_param = ParameterUtil.getParameter from_formal_params, idx

        if param_match[0] == idx && param_match[1] == idx
          Log.info "exact match"
        elsif param_match[0] == idx
          function_mark_parameter_name_changed_xxx from_formal_param, to_formal_params, idx
        elsif param_match[1] == idx
          function_mark_parameter_type_changed_xxx from_param, to_formal_params, idx
        elsif param_match[0] >= 0
          function_check_for_reorder_xxx from_formal_param, idx, to_formal_params, param_match[0]
        elsif param_match[1] >= 0
          function_mark_reordered_xxx from_formal_param, idx, to_formal_params, param_match[1]
        else
          function_mark_removed_xxx from_formal_param, to_formal_params
        end
      end

      to_params.each_with_index do |to_param, to_idx|
        if to_param
          to_formal_param = ParameterUtil.getParameter to_formal_params, to_idx
          to_name = ParameterUtil.getParameterName to_formal_param
          changed from_formal_params, to_formal_param, PARAMETER_ADDED, to_name.image
        end
      end
    end

    def function_check_for_reorder_xxx from_param, from_idx, to_formal_params, to_idx
      from_name_tk = ParameterUtil.getParameterName from_param
      to_name_tk = ParameterUtil.getParameterName to_formal_params, to_idx
      if from_name_tk.image == to_name_tk.image
        changed from_name_tk, to_name_tk, PARAMETER_REORDERED, from_name_tk.image, from_idx, to_idx
      else
        changed from_name_tk, to_name_tk, PARAMETER_REORDERED_AND_RENAMED, from_name_tk.image, from_idx, to_idx, to_name_tk.image
      end
    end

    # $$$ @untested
    def function_mark_reordered_xxx from_param, from_idx, to_params, to_idx
      from_name_tk = ParameterUtil.getParameterName from_param
      to_param = ParameterUtil.getParameter(to_params, to_idx)
      changed from_param, to_param, PARAMETER_REORDERED, from_name_tk.image, from_idx, to_idx
    end

    def function_mark_removed_xxx from_param, to_params
      from_name_tk = ParameterUtil.getParameterName from_param
      changed from_param, to_params, PARAMETER_REMOVED, from_name_tk.image
    end

    def function_mark_parameter_type_changed_xxx from_param, to_formal_params, idx
      to_param = ParameterUtil.getParameter to_formal_params, idx
      to_type = ParameterUtil.getParameterType to_param
      changed from_param.getParameter(), to_param, PARAMETER_TYPE_CHANGED, from_param.getType(), to_type
    end

    def function_mark_parameter_name_changed_xxx from_param, to_formal_params, idx
      from_name_tk = ParameterUtil.getParameterName from_param
      to_name_tk = ParameterUtil.getParameterName to_formal_params, idx
      changed from_name_tk, to_name_tk, PARAMETER_NAME_CHANGED, from_name_tk.image, to_name_tk.image
    end

    def function_mark_parameters_added_xxx from_formal_params, to_formal_params
      names = ParameterUtil.getParameterNames to_formal_params
      names.each do |name|
        changed from_formal_params, name, PARAMETER_ADDED, name.image
      end
    end

    def function_mark_parameters_removed_xxx from_formal_params, to_formal_params
      names = ParameterUtil.getParameterNames from_formal_params
      names.each do |name|
        changed name, to_formal_params, PARAMETER_REMOVED, name.image
      end
    end
    
    def function_compare_parameters_xxx from_params, to_params
      from_param_list = ParameterUtil.getParameterList from_params
      to_param_list = ParameterUtil.getParameterList to_params
        
      from_param_types = ParameterUtil.getParameterTypes from_params
      to_param_types = ParameterUtil.getParameterTypes to_params

      from_size = from_param_types.size
      to_size = to_param_types.size

      if from_size > 0
        if to_size > 0
          function_compare_each_parameter_xxx from_params, from_param_list, to_params, to_param_list, from_size
        else
          function_mark_parameters_removed_xxx from_params, to_params
        end
      elsif to_size > 0
        function_mark_parameters_added_xxx from_params, to_params
      end
    end

    def function_change_throws_xxx from_node, to_node, msg, name
      changed from_node, to_node, msg, SimpleNodeUtil.toString(name)
    end

    def function_add_all_throws_xxx from_node, to_name_list
      names = getChildNames to_name_list
      names.each do |name|
        function_change_throws_xxx from_node, name, THROWS_ADDED, name
      end
    end

    def function_remove_all_throws_xxx from_name_list, to_node
      names = getChildNames from_name_list
      names.each do |name|
        function_change_throws_xxx name, to_node, THROWS_REMOVED, name
      end
    end

    def function_compare_each_throw_xxx from_name_list, to_name_list
      from_names = getChildNames from_name_list
      to_names = getChildNames to_name_list

      (0 ... from_names.size()).each do |from_idx|
        # save a reference to the name here, in case it gets removed
        # from the array in getMatch.
        from_name = from_names.get from_idx
        
        throws_match = getMatch from_names, from_idx, to_names

        if throws_match == from_idx
          next
        elsif throws_match >= 0
          to_name = ThrowsUtil.getNameNode to_name_list, throws_match
          from_name_str = SimpleNodeUtil.toString from_name
          changed from_name, to_name, THROWS_REORDERED, from_name_str, from_idx, throws_match
        else
          function_change_throws_xxx from_name, to_name_list, THROWS_REMOVED, from_name
        end
      end

      (0 ... to_names.size()).each do |to_idx|
        if to_names.get(to_idx)
          to_name = ThrowsUtil.getNameNode to_name_list, to_idx
          function_change_throws_xxx from_name_list, to_name, THROWS_ADDED, to_name
        end
      end
    end

    def function_compare_throws_xxx from_node, from_name_list, to_node, to_name_list
      if from_name_list.nil?
        if to_name_list
          function_add_all_throws_xxx from_node, to_name_list
        end
      elsif to_name_list.nil?
        function_remove_all_throws_xxx from_name_list, to_node
      else
        function_compare_each_throw_xxx from_name_list, to_name_list
      end
    end
  end
end
