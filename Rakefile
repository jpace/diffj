require 'rubygems'
require 'rake'
require 'java'
require 'rake/testtask'

include Java

require 'ant'

# this is fixed in JRuby 1.6.0:
$CLASSPATH << "#{ENV['JAVA_HOME']}/lib/tools.jar"

$jrubycompletejar = "libs/jruby-complete-1.6.3.jar"
$pmdjar = "libs/pmd-4.2.5.jar"
$junitjar = "libs/junit-4.10.jar"
$diffjjar = "staging/libs/diffj-1.2.1.jar"

$CLASSPATH << $diffjjar << $jrubycompletejar << $pmdjar

$clsmaindir = 'staging/classes/main'
$clstestdir = 'staging/classes/test'
$clsjrubydir = 'staging/classes/jruby'

$srcmainjavadir = 'src/main/java'
$srcmainrubydir = 'src/main/ruby'
$srcmainjrubydir = 'src/main/jruby'

$srctestjavadir = 'src/test/java'
$srctestrubydir = 'src/test/ruby'

$buildlibsdir = 'staging/libs'

$reportdir = 'staging/report'

directory $clsmaindir
directory $clstestdir
directory $buildlibsdir
directory $clsjrubydir
directory $reportdir

buildjars = [ $jrubycompletejar, $pmdjar ]
testjars =  [ $junitjar ]

task :setup do
  ant.path :id => 'classpath' do
    buildjars.each do |jarfile|
      fileset :file => jarfile
    end
  end

  ant.path :id => 'test.classpath' do
    pathelement :location => $clsmaindir
    path        :refid    => 'classpath'
    testjars.each do |jarfile|
      fileset :file => jarfile
    end
    pathelement :location => $clstestdir
  end
end

task "java:compile" => [ :setup, $clsmaindir ] do
  ant.javac(:destdir => $clsmaindir, 
            :srcdir => $srcmainjavadir,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task "jruby:compile" => [ :setup, $clsmaindir ] do
  ant.javac(:destdir => $clsmaindir, 
            :srcdir => $srcmainjrubydir,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task "java:tests:compile" => [ :setup, $clstestdir, "java:compile" ] do
  ant.javac(:destdir => $clstestdir, 
            :srcdir => $srctestjavadir,
            :classpathref => 'test.classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task "java:jar" => [ "java:compile", $buildlibsdir ] do
  ant.jar(:jarfile => $diffjjar, 
          :basedir => $clsmaindir)
end

task "jruby:jar" => [ "java:compile", "jruby:compile" ] do
  sh "jar -cfm diffj.jar src/main/jar/launcher.manifest -C #{$clsmaindir} . -C #{$srcmainrubydir} . -C tmp ."
end

task "java:tests" => [ "java:tests:compile", $reportdir ] do  
  ant.junit(:fork => "yes", :forkmode => "once", :printsummary => "yes",  
            :showoutput => true,
            :haltonfailure => "no", :failureproperty => "tests.failed") do  
    classpath :refid => 'test.classpath'  
    formatter :type => "xml"
    batchtest :todir => $reportdir do  
      fileset :dir => $srctestjavadir, :includes => '**/Test*.java'  
    end  
  end  
  if ant.project.getProperty "tests.failed"
    ant.junitreport :todir => $reportdir do  
      fileset :dir => $reportdir, :includes => "TEST-*.xml"  
      report :todir => "#{$reportdir}/html"  
    end  
    ant.fail :message => "Test(s) failed. Report is at #{$reportdir}/html."
  end  
end

class DiffJRakeTestTask < Rake::TestTask
  def initialize name, filter = name
    super(('test:' + name) => "java:tests:compile") do |t|
      t.libs << $srcmainrubydir
      t.libs << $srctestrubydir
      t.pattern = "#{$srctestrubydir}/**/#{filter}/**/test*.rb"
      t.warning = true
      t.verbose = true
    end
  end
end

DiffJRakeTestTask.new('imports')
DiffJRakeTestTask.new('ctor')
DiffJRakeTestTask.new('field')
DiffJRakeTestTask.new('method')
DiffJRakeTestTask.new('type')
DiffJRakeTestTask.new('types')

DiffJRakeTestTask.new('method/body/zeroone')
DiffJRakeTestTask.new('method/parameters/zeroone')
DiffJRakeTestTask.new('method/throws/zeroone')
DiffJRakeTestTask.new('method/parameters/reorder')
DiffJRakeTestTask.new('method/parameters/reorder/typechange')

DiffJRakeTestTask.new('all', '*')

task "jruby:tests" => "test:all"

