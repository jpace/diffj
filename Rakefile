require 'rubygems'
require 'fileutils'

require 'lib/tasks/common'

task :default => :buildjar

$fname    = "diffj.rb"
$clsname  = "DiffjMain.class"

$builddir   = "build/jruby"

$metainfdir = "META-INF"
$mfname     = $metainfdir + "/MANIFEST.MF"
$mainclass  = "DiffJMain"

$jrubyjar   = "/home/jpace/Downloads/jruby-complete-1.6.3.jar"
$tgtjar     = "diffj-x.y.z.jar"

$rbfiles = %w{ 
  diffj.rb
}

directory $builddir

directory buildfile($metainfdir)

copytask $mfname, [ buildfile($metainfdir), "src/main/java/#{$mfname}" ], :manifest

copytask $tgtjar, [ $jrubyjar ], :tgtjar

copygroup $rbfiles, :rbfiles

jrubyctask $fname, :rbmain

task :jrubyc => $fname do |t|
  puts "running: jrubyc -t #{$builddir} --javac #{t.prerequisites.last}"
  sh "jrubyc -t #{$builddir} --javac #{t.prerequisites.last}"
end

copytask $clsname, [ $clsname ], :javaclass
  
task :buildjar => [ :manifest, :tgtjar, :rbmain, :rbfiles ] do
  Dir.chdir $builddir

  sh "jar ufm #{$tgtjar} #{$mfname} *.class #{$rbfiles.join(' ')}"
end
