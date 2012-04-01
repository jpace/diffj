#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'

include Java

import org.incava.diffj.MethodDiff
import org.incava.diffj.TypeMatches
import org.incava.diffj.TypeMethodDiff
import org.incava.pmdx.MethodUtil

module DiffJ; end

class DiffJ::MethodDeclComparator < TypeMethodDiff
  include Loggable

  def initialize diffs
    super diffs
  end

  # this is from the superclass, not TypeMethodDiff:
  def compare_xxx from_coid, to_coid
    info "from_coid: #{from_coid}; #{from_coid.class}".on_green
    info "to_coid: #{to_coid}; #{to_coid.class}".on_green

    from_decls = declarations_of_class_type_xxx from_coid
    to_decls = declarations_of_class_type_xxx to_coid

    matches = get_type_matches_xxx from_decls, to_decls

    from_unproc = java.util.ArrayList.new from_decls
    to_unproc = java.util.ArrayList.new to_decls

    compare_matches_xxx matches, from_unproc, to_unproc

    add_removed_xxx from_unproc, to_coid
    add_added_xxx from_coid, to_unproc
  end

  def do_compare_xxx from, to
    differ = MethodDiff.new(getFileDiffs())
    differ.compareAccess(SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to))
    differ.compare(from, to)
  end

  def compare_matches_xxx matches, unprocA, unprocB
    descendingScores = matches.getDescendingScores()
    descendingScores.each do |score|
      procA = java.util.ArrayList.new
      procB = java.util.ArrayList.new

      matches.get(score).each do |declPair|
        amd = declPair.getFirst()
        bmd = declPair.getSecond()

        if unprocA.contains(amd) && unprocB.contains(bmd)
          do_compare_xxx amd, bmd
          
          procA.add amd
          procB.add bmd
        end
      end

      unprocA.removeAll procA
      unprocB.removeAll procB
    end
  end

  def get_declarations_of_class_xxx decls
    declList = java.util.ArrayList.new

    decls.each do |decl|
      dec = TypeDeclarationUtil.getDeclaration(decl, getClassName())

      if dec
        declList.add(dec)
      end
    end
    declList
  end

  def declarations_of_class_type_xxx coid
    decls = TypeDeclarationUtil.getDeclarations coid
    get_declarations_of_class_xxx decls
  end

  def get_type_matches_xxx amds, bmds
    matches = TypeMatches.new
    amds.each do |amd|
      bmds.each do |bmd|
        score = MethodUtil.getMatchScore amd, bmd
        if score > 0.0
          matches.add score, amd, bmd
        end
      end
    end
    info "matches: #{matches}".yellow    
    matches
  end

  def get_name_xxx methdecl
    MethodUtil.getFullName methdecl
  end

  def add_added_xxx from_coid, to
    to.each do |t|
      name = get_name_xxx t
      added from_coid, t, getAddedMessage(t), name
    end
  end

  def add_removed_xxx from, to_coid
    from.each do |f|
      name = get_name_xxx f
      deleted f, to_coid, getRemovedMessage(f), name
    end
  end
end
