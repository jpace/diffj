#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'
require 'java'
require 'diffj/ast/item'

include Java

class DiffJ::TypeItemDeclComparator < DiffJ::ItemComparator
  include Loggable

  def initialize diffs, clsname
    super diffs
    @clsname = clsname
  end

  def compare from_coid, to_coid
    fromdecls = declarations_of_class_type from_coid
    todecls = declarations_of_class_type to_coid

    matches = get_type_matches fromdecls, todecls

    from_unproc = fromdecls.dup
    to_unproc = todecls.dup

    compare_matches matches, from_unproc, to_unproc

    add_removed from_unproc, to_coid
    add_added from_coid, to_unproc
  end

  def get_type_matches frommds, tomds
    matches = Hash.new { |h, k| h[k] = Array.new }
    
    frommds.each do |frommd|
      tomds.each do |tomd|
        info "frommd: #{frommd.to_string}".yellow
        info "tomd: #{tomd.to_string}".yellow

        score = get_score frommd, tomd
        info "score: #{score}".yellow

        if score > 0.0
          matches[score] << [ frommd, tomd ]
        end
      end
    end
    matches
  end

  def compare_matches matches, fromunproc, tounproc
    info "matches: #{matches}".yellow
    
    matches.sort.reverse.each do |score, decls|
      fromproc = Array.new
      toproc = Array.new

      decls.each do |decl|
        frommd = decl[0]
        tomd = decl[1]

        if fromunproc.include?(frommd) && tounproc.include?(tomd)
          do_compare frommd, tomd
          
          fromproc << frommd
          toproc << tomd
        end
      end

      fromunproc.reject! { |fp| fromproc.include? fp }
      tounproc.reject! { |fp| toproc.include? fp }
    end
  end

  def get_declarations_of_class decls
    decls.collect { |decl| decl.declaration(@clsname) }.compact
  end

  def declarations_of_class_type coid
    get_declarations_of_class coid.declarations
  end

  def get_score amd, bmd
    raise "abstract method!"
  end

  def add_added from_coid, to
    to.each do |t|
      added from_coid, t, get_added_message(t), get_name(t)
    end
  end

  def add_removed from, to_coid
    from.each do |f|
      deleted f, to_coid, get_removed_message(f), get_name(f)
    end
  end
end
