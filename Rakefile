require 'rubygems'
require 'fileutils'
require 'java'

puts "CLASSPATH: #{$CLASSPATH}"
puts "CLASSPATH.class: #{$CLASSPATH.class}"

$CLASSPATH << "build/libs/diffj-1.2.1.jar"
$CLASSPATH << "libs/jruby-complete-1.6.3.jar"
$CLASSPATH << "libs/pmd-4.2.5.jar" # this gets mushed into diffj-1.2.1.jar, but for future builds it won't.

task :nothing do
  puts "nothing"
end

require 'ant'
require 'lib/tasks/common'

if false
  puts "$CLASSPATH: #{$CLASSPATH}"
  $CLASSPATH << ":build/libs/diffj-1.2.1.jar:libs/jruby-complete-1.6.3.jar:libs/pmd-4.2.5.jar"
  puts "$CLASSPATH: #{$CLASSPATH}"

  puts "ENV: #{$ENV}"

  task :default => :buildjar

  $fname    = "diffj.rb"
  $clsname  = "DiffjMain.class"

  $builddir   = "build/jruby"

  $metainfdir = "META-INF"
  $mfname     = $metainfdir + "/MANIFEST.MF"
  $mainclass  = "DiffJMain"

  $jrubyjar   = "/home/jpace/Downloads/jruby-complete-1.6.3.jar"
  $tgtjar     = "diffj-x.y.z.jar"

  $rbfiles = %w{ diffj.rb }

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
end

# beginning of cleaned up Rake code:

task :runtest do
  sh "jruby ./src/test/ruby/test_diffj.rb"
end
