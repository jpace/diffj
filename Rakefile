require 'rubygems'
require 'rake'
require 'java'
require 'rake/testtask'

include Java

require 'ant'

# this is fixed in JRuby 1.6.0:
$CLASSPATH << "#{ENV['JAVA_HOME']}/lib/tools.jar"

$CLASSPATH << "build/libs/diffj-1.2.1.jar"
$CLASSPATH << "libs/jruby-complete-1.6.3.jar"
$CLASSPATH << "libs/pmd-4.2.5.jar"

$clsmaindir = 'staging/classes/main'
$clstestdir = 'staging/classes/test'
$clsjrubydir = 'staging/classes/jruby'

$srcmainjavadir = 'src/main/java'
$srcmainrubydir = 'src/main/ruby'
$srcmainjrubydir = 'src/main/jruby'

$srctestjavadir = 'src/test/java'
$srctestrubydir = 'src/test/ruby'

$jarfname = 'diffj-1.2.1.jar'
$buildlibsdir = 'staging/libs'
$destjarfile = $buildlibsdir + '/' + $jarfname

directory $clsmaindir
directory $clstestdir
directory $buildlibsdir
directory $clsjrubydir

buildjars = [ 'libs/jruby-complete-1.6.3.jar', 'libs/pmd-4.2.5.jar' ]
testjars =  [ 'libs/junit-4.10.jar' ]

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
  end
end

task :compile => [ :setup, $clsmaindir ] do
  ant.javac(:destdir => $clsmaindir, 
            :srcdir => $srcmainjavadir,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task :jruby_compile => [ :setup, $clsmaindir ] do
  ant.javac(:destdir => $clsmaindir, 
            :srcdir => $srcmainjrubydir,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task :testscompile => [ :setup, $clstestdir, :compile ] do
  ant.javac(:destdir => $clstestdir, 
            :srcdir => $srctestjavadir,
            :classpathref => 'test.classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task "tests:java" => [ :testscompile ] do
  ant.javac(:destdir => $clstestdir, 
            :srcdir => $srctestjavadir,
            :classpathref => 'test.classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task :jar => [ :compile, $buildlibsdir ] do
  ant.jar(:jarfile => $destjarfile, 
          :basedir => $clsmaindir)
end

task :diffj_jar_build => [ :compile, :jruby_compile ] do
  sh "jar -cfm diffj.jar src/main/jar/launcher.manifest -C staging/classes/main . -C src/main/ruby . -C tmp ."
end

class DiffJRakeTestTask < Rake::TestTask
  def initialize name, filter = name
    super(('test:' + name) => :testscompile) do |t|
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
