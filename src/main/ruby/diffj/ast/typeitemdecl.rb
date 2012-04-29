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
    # info "frommds: #{frommds}; #{frommds.class}"
    # info "@clsname: #{@clsname}"

    if @clsname == "net.sourceforge.pmd.ast.ASTMethodDeclaration"
      get_type_matches_for_methods frommds, tomds
    else
      matches = Hash.new { |h, k| h[k] = Array.new }
      
      frommds.each do |frommd|
        tomds.each do |tomd|
          score = get_score frommd, tomd
          if score > 0.0
            matches[score] << [ frommd, tomd ]
          end
        end
      end
      matches
    end
  end

  def get_methods_by_name meths
    meths_by_name = Hash.new { |h, k| h[k] = Array.new }
    meths.each do |meth|
      meths_by_name[meth.name.image] << meth
    end
    meths_by_name
  end

  def get_type_matches_for_methods frommds, tomds
    matches = Hash.new { |h, k| h[k] = Array.new }

    frommds_by_name = get_methods_by_name frommds
    # info "frommds_by_name: #{frommds_by_name.inspect}".bold.green   

    tomds_by_name = get_methods_by_name tomds
    # info "tomds_by_name: #{tomds_by_name.inspect}".bold.green
    
    common_methods = frommds_by_name.keys & tomds_by_name.keys
    # info "common_methods: #{common_methods.inspect}".bold.yellow
    
    common_methods.each do |methname|
      # info "methname: #{methname}"
      froms = frommds_by_name[methname]
      nfroms = froms.size

      froms.each_with_index do |frommd, fidx|
        tos = tomds_by_name[methname]
        ntos = tos.size

        tos.each_with_index do |tomd, tidx|
          # info "#{fidx} of #{nfroms}, #{tidx} of #{ntos}"
          # info "frommd: #{frommd.to_string}; tomd: #{tomd.to_string}"
          score = get_score frommd, tomd
          if score > 0.0
            matches[score] << [ frommd, tomd ]
          end
        end
      end
    end
    matches
  end

  def compare_matches matches, fromunproc, tounproc
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
