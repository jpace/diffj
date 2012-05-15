#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel/log'
require 'java'
require 'diffj/ast/item'

include Java

module DiffJ
  module ParamDiff
    def types_equal? from, to
      return from && from.typestr == to.typestr
    end

    def names_equal? from, to
      return from && from.namestr == to.namestr
    end
  end

  class ParamMatchData
    attr_accessor :type
    attr_accessor :name

    def initialize
      @type = nil
      @name = nil
    end

    def exact_match? idx
      @name == idx && @type == idx
    end
  end

  class ParamLists
    include ParamDiff, Loggable

    attr_reader :from
    attr_reader :to
    
    def initialize from, to
      @from = from
      @to = to
    end

    def clear_from_lists fromidx, toidx
      @from[fromidx] = nil
      @to[toidx] = nil
    end

    def get_param_matches fromidx
      # a little tribute here:
      pmd = ParamMatchData.new

      fp = @from[fromidx]

      (0 ... @to.size).each do |toidx|
        tp = @to[toidx]
        next unless tp

        typematched = false
        if types_equal? fp, tp
          pmd.type = toidx
          typematched = true
        end

        if names_equal? fp, tp
          pmd.name = toidx
          if typematched
            # got an exact match:
            return pmd
          end
        end
      end
      pmd
    end
    
    # returns whether there is a matching element in the from list
    def has_from_match? toidx
      to = @to[toidx]
      @from.index do |from|
        types_equal?(from, to) && names_equal?(from, to)
      end
    end
    
    def process_match fromidx
      nomatch = ParamMatchData.new
      
      pmd = get_param_matches fromidx
      if pmd.type && pmd.type == pmd.name
        clear_from_lists fromidx, pmd.name
        return pmd
      end

      bestmatch = pmd.type || pmd.name
        
      # make sure there isn't an exact match for this somewhere else in
      # from_parameters
      if !bestmatch || has_from_match?(bestmatch)
        nomatch
      else
        clear_from_lists fromidx, bestmatch
        pmd
      end
    end
  end

  class FunctionComparator < ItemComparator
    include ParamDiff

    RETURN_TYPE_CHANGED = "return type changed from {0} to {1}"

    PARAMETER_REMOVED = "parameter removed: {0}"
    PARAMETER_ADDED = "parameter added: {0}"
    PARAMETER_REORDERED = "parameter {0} reordered from argument {1} to {2}"

    PARAMETER_TYPE_CHANGED = "parameter type changed from {0} to {1}"
    PARAMETER_NAME_CHANGED = "parameter name changed from {0} to {1}"
    PARAMETER_REORDERED_AND_RENAMED = "parameter {0} reordered from argument {1} to {2} and renamed {3}"
    PARAMETER_REORDERED_AND_TYPE_CHANGED = "parameter {0} reordered from argument {1} to {2} and changed type from {3} to {4}"

    THROWS_REMOVED = "throws removed: {0}"
    THROWS_ADDED = "throws added: {0}"
    THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}"
    
    def compare_return_types from, to
      fromrettype = from[0]
      torettype = to[0]
      fromrettypestr = fromrettype.to_string
      torettypestr = torettype.to_string

      if fromrettypestr != torettypestr
        changed fromrettype, torettype, RETURN_TYPE_CHANGED, fromrettypestr, torettypestr
      end
    end

    def get_child_names namelist
      namelist.find_children "net.sourceforge.pmd.ast.ASTName"
    end

    def compare_each_parameter from_formal_params, to_formal_params, size
      from_param_list = from_formal_params.parameters
      to_param_list = to_formal_params.parameters

      paramlists = ParamLists.new from_param_list, to_param_list

      (0 ... size).each do |idx|
        # save this, since process_match might clear it from the list:
        from_param = paramlists.from[idx]
        
        pmd = paramlists.process_match idx

        # exact match:
        next if pmd.exact_match?(idx)

        from_formal_param = from_formal_params.get_parameter idx

        if pmd.type == idx
          mark_parameter_name_changed from_formal_param, to_formal_params, idx
        elsif pmd.name == idx
          mark_parameter_type_changed from_param, to_formal_params, idx
        elsif pmd.type
          check_for_reorder from_formal_param, idx, to_formal_params, pmd.type
        elsif pmd.name
          mark_reordered_and_type_changed from_formal_param, idx, to_formal_params, pmd.name
        else
          mark_removed from_formal_param, to_formal_params
        end
      end

      paramlists.to.each_with_index do |toparam, toidx|
        next unless toparam
        
        to_formal_param = to_formal_params.get_parameter toidx
        toname = to_formal_param.namestr
        changed from_formal_params, to_formal_param, PARAMETER_ADDED, toname
      end
    end

    def check_for_reorder from_param, from_idx, to_formal_params, to_idx
      fromnametk = from_param && from_param.nametk
      tonametk = to_formal_params.get_parameter_nametk to_idx
      if fromnametk.image == tonametk.image
        changed fromnametk, tonametk, PARAMETER_REORDERED, fromnametk.image, from_idx, to_idx
      else
        changed fromnametk, tonametk, PARAMETER_REORDERED_AND_RENAMED, fromnametk.image, from_idx, to_idx, tonametk.image
      end
    end

    def mark_reordered_and_type_changed from_param, fromidx, to_params, toidx
      toparam = to_params.get_parameter toidx
      changed from_param, toparam, PARAMETER_REORDERED_AND_TYPE_CHANGED, from_param.namestr, fromidx, toidx, from_param.typestr, toparam.typestr
    end

    def mark_removed fromparam, toparams
      changed fromparam, toparams, PARAMETER_REMOVED, fromparam.namestr
    end

    def mark_parameter_type_changed fromparam, toformalparams, idx
      toparam = toformalparams.get_parameter idx
      changed fromparam, toparam, PARAMETER_TYPE_CHANGED, fromparam.typestr, toparam.typestr
    end

    def mark_parameter_name_changed fromparam, toformalparams, idx
      fromnametk = fromparam.nametk
      tonametk = toformalparams.get_parameter_nametk idx
      changed fromnametk, tonametk, PARAMETER_NAME_CHANGED, fromnametk.image, tonametk.image
    end

    def mark_parameters_added fromformalparams, toformalparams
      names = toformalparams.get_parameter_names 
      names.each do |name|
        changed fromformalparams, name, PARAMETER_ADDED, name.image
      end
    end

    def mark_parameters_removed fromformalparams, toformalparams
      names = fromformalparams.get_parameter_names 
      names.each do |name|
        changed name, toformalparams, PARAMETER_REMOVED, name.image
      end
    end
    
    def compare_parameters from_params, to_params
      from_param_types = from_params.get_parameter_types
      to_param_types = to_params.get_parameter_types

      fromsize = from_param_types.size
      tosize = to_param_types.size

      if fromsize > 0
        if tosize > 0
          compare_each_parameter from_params, to_params, fromsize
        else
          mark_parameters_removed from_params, to_params
        end
      elsif tosize > 0
        mark_parameters_added from_params, to_params
      end
    end

    def change_throws fromnode, tonode, msg, name
      changed fromnode, tonode, msg, name.to_string
    end

    def add_all_throws fromnode, tonamelist
      names = get_child_names tonamelist
      names.each do |name|
        change_throws fromnode, name, THROWS_ADDED, name
      end
    end

    def remove_all_throws fromnamelist, tonode
      names = get_child_names fromnamelist
      names.each do |name|
        change_throws name, tonode, THROWS_REMOVED, name
      end
    end

    def process_throws_match fromnames, fromidx, tonames
      fromnamestr = fromnames[fromidx].to_string

      (0 ... tonames.size).each do |toidx|
        toname = tonames[toidx]
        if toname && toname.to_string == fromnamestr
          fromnames[fromidx] = nil
          tonames[toidx] = nil # mark as consumed
          return toidx
        end
      end
      nil
    end

    def compare_each_throw from_name_list, to_name_list
      fromnames = get_child_names from_name_list
      tonames = get_child_names to_name_list

      (0 ... fromnames.size).each do |fromidx|
        # save a reference to the name here, in case it gets removed
        # from the array in getMatch.
        fromname = fromnames[fromidx]
        
        throws_match = process_throws_match fromnames, fromidx, tonames

        if throws_match.nil?
          change_throws fromname, to_name_list, THROWS_REMOVED, fromname
        elsif throws_match == fromidx
          next
        elsif throws_match
          toname = to_name_list.name_node throws_match
          fromnamestr = fromname.to_string
          changed fromname, toname, THROWS_REORDERED, fromnamestr, fromidx, throws_match
        end
      end

      (0 ... tonames.size).each do |toidx|
        if tonames[toidx]
          toname = to_name_list.name_node toidx
          change_throws from_name_list, toname, THROWS_ADDED, toname
        end
      end
    end

    def compare_throws from_node, from_name_list, to_node, to_name_list
      if from_name_list.nil?
        if to_name_list
          add_all_throws from_node, to_name_list
        end
      elsif to_name_list.nil?
        remove_all_throws from_name_list, to_node
      else
        compare_each_throw from_name_list, to_name_list
      end
    end
  end
end
