require 'rubygems'
require 'fileutils'

require 'lib/tasks/common'

task :default => :buildjrubyjar

$fname    = "mackworth.rb"
$clsname  = "MackworthTestMain.class"

$builddir   = "build"

$metainfdir = "META-INF"
$mfname     = $metainfdir + "/MANIFEST.MF"

$jrubyjar   = "/home/jpace/Downloads/jruby-complete-1.6.3.jar"
$tgtjar     = "mackworth.jar"

$rbfiles = %w{ 
  csvfile.rb
  drawer.rb
  panel.rb
  spacebarlistener.rb
  swingutil.rb
  testframe.rb
}

directory $builddir

directory buildfile($metainfdir)

copytask $mfname, [ buildfile($metainfdir), "jar/#{$mfname}" ], :manifest
copytask $tgtjar, [ $jrubyjar ], :tgtjar

copygroup $rbfiles, :rbfiles

jrubyctask $fname, :rbmain

task :jrubyc => $fname do |t|
  sh "jrubyc -t #{$builddir} --javac #{t.prerequisites.last}"
end

copytask $clsname, [ $clsname ], :javaclass
  
task :buildjrubyjar => [ :manifest, :tgtjar, :rbmain, :rbfiles ] do
  Dir.chdir $builddir

  sh "jar ufm #{$tgtjar} #{$mfname} *.class #{$rbfiles.join(' ')}"
end
