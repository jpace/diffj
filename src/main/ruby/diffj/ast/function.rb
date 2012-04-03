#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

module DiffJ
  module FunctionComparator # < MethodDiff

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

    def function_compare_parameters_xxx from_params, to_params
      from_param_list = ParameterUtil.getParameterList from_params
      to_param_list = ParameterUtil.getParameterList to_params
        
      from_param_types = ParameterUtil.getParameterTypes from_params
      to_param_types = ParameterUtil.getParameterTypes to_params

      from_size = from_param_types.size
      to_size = to_param_types.size

      if from_size > 0
        if to_size > 0
          compareEachParameter from_params, from_param_list, to_params, to_param_list, from_size
        else
          markParametersRemoved from_params, to_params
        end
      elsif to_size > 0
        markParametersAdded from_params, to_params
      end
    end

  end
end
