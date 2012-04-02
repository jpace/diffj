#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

import org.incava.diffj.DiffComparator
import org.incava.diffj.MethodDiff
import org.incava.diffj.TypeMatches
import org.incava.diffj.TypeMethodDiff
import org.incava.pmdx.MethodUtil

module DiffJ; end

class DiffJ::TypeItemDeclComparator < DiffJ::ItemComparator
  include Loggable

  def initialize diffs, clsname
    super diffs
    @clsname = clsname
  end

  # this is from the superclass, not TypeMethodDiff:
  def compare from_coid, to_coid
    info "from_coid: #{from_coid}; #{from_coid.class}".on_green
    info "to_coid: #{to_coid}; #{to_coid.class}".on_green

    from_decls = declarations_of_class_type from_coid
    to_decls = declarations_of_class_type to_coid

    matches = get_type_matches from_decls, to_decls

    from_unproc = from_decls.dup
    to_unproc = to_decls.dup

    compare_matches matches, from_unproc, to_unproc

    add_removed from_unproc, to_coid
    add_added from_coid, to_unproc
  end

  def compare_matches matches, from_unproc, to_unproc
    descendingScores = matches.getDescendingScores()
    descendingScores.each do |score|
      from_proc = Array.new
      to_proc = Array.new

      matches.get(score).each do |declPair|
        amd = declPair.getFirst()
        bmd = declPair.getSecond()

        if from_unproc.include?(amd) && to_unproc.include?(bmd)
          do_compare amd, bmd
          
          from_proc << amd
          to_proc << bmd
        end
      end

      from_unproc.reject! { |fp| from_proc.include?(fp) }
      to_unproc.reject! { |fp| to_proc.include?(fp) }
    end
  end

  def get_declarations_of_class decls
    decllist = Array.new
    decls.each do |decl|
      if dec = TypeDeclarationUtil.getDeclaration(decl, @clsname)
        decllist << dec
      end
    end
    decllist
  end

  def declarations_of_class_type coid
    decls = TypeDeclarationUtil.getDeclarations coid
    get_declarations_of_class decls
  end

  def get_score amd, bmd
    raise "abstract method!"
  end

  def get_type_matches amds, bmds
    matches = TypeMatches.new
    amds.each do |amd|
      bmds.each do |bmd|
        score = get_score amd, bmd
        if score > 0.0
          matches.add score, amd, bmd
        end
      end
    end
    info "matches: #{matches}".yellow    
    matches
  end

  def add_added from_coid, to
    to.each do |t|
      name = get_name t
      added from_coid, t, get_added_message(t), name
    end
  end

  def add_removed from, to_coid
    from.each do |f|
      name = get_name f
      deleted f, to_coid, get_removed_message(f), name
    end
  end
end
