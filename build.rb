#!/usr/bin/jruby -w
# -*- ruby -*-

require 'rubygems'
require 'riel'

$startdir = Pathname.pwd

$builddir = Pathname.new "target"
$diffjjarfile = ($builddir + "diffj-x.y.z.jar").expand_path
$jrubyjar   = Pathname.new("libs/jruby-complete-1.6.3.jar")
$mfname     = Pathname.new("src/main/java/META-INF/MANIFEST.MF").expand_path
$diffjrb    = Pathname.new("src/main/ruby/diffj.rb")
$origjar    = Pathname.new("libs/diffj-dep-1.0.0.jar")

class JarFile
end

class Project
  include Loggable
  
  def run cmd, verbose = true
    puts "cmd: #{cmd}".yellow
    IO.popen(cmd) do |io|
      io.each do |line|
        puts line if verbose
      end
    end
  end

  def gradle_build
    # depends on .java files being newer ...
    run "gradle build"
  end

  def copy_jruby_jarfile
    run "cp #{$origjar} #{$diffjjarfile}"
  end

  def copy_original_jarfile
    run "cp #{$origjar} #{$diffjjarfile}"
  end

  def compile_jruby_code
    run "jrubyc -t #{$builddir} --javac #{$diffjrb}"
  end

  def update_jarfile_classes dir
    Dir.chdir dir
    run "find -name '*.class' | xargs jar uf #{$diffjjarfile}"
    Dir.chdir $startdir
  end
  
  def update_manifest
    # or do java ufe $diffjjarfile ...DiffJMain 
    # run "jar ufm #{$diffjjarfile} #{$mfname} 2>&1", false
    run "jar ufe #{$diffjjarfile} DiffJMain"
  end

  def update_jarfile_ruby_files
    run "jar uf #{$diffjjarfile} #{$diffjrb}"
  end

  def build opts
    gradle_build unless opts[:gradle_build] == false
    copy_jruby_jarfile
    update_jarfile_classes "build/classes/main"

    compile_jruby_code
    update_jarfile_classes $builddir
    update_jarfile_ruby_files

    update_manifest
  end
end

opts = Hash.new

ARGV.each do |arg|
  case arg
  when "-G"
    opts[:gradle_build] = false
  end
end

p = Project.new

p.build opts
