require 'rubygems'
require 'rake'
require 'java'
require 'rake/testtask'
require 'rake/packagetask'
require 'pathname'

include Java

require 'ant'

DIFFJ_VERSION    = "1.3.0"
DIFFJ_NAME      = "diffj"
DIFFJ_FULLNAME  = "#{DIFFJ_NAME}-#{DIFFJ_VERSION}"

# directories - Gradle/Maven layout (mostly)

SRC_DIR            = 'src'
SRC_MAIN_DIR       = SRC_DIR      + '/main'
SRC_MAIN_JAVA_DIR  = SRC_MAIN_DIR + '/java'
SRC_MAIN_RUBY_DIR  = SRC_MAIN_DIR + '/ruby'
SRC_MAIN_JRUBY_DIR = SRC_MAIN_DIR + '/jruby'

SRC_TEST_DIR       = SRC_DIR      + '/test'
SRC_TEST_JAVA_DIR  = SRC_TEST_DIR + '/java'
SRC_TEST_RUBY_DIR  = SRC_TEST_DIR + '/ruby'

STAGING_DIR        = 'staging'
STAGING_CLS_DIR    = STAGING_DIR + '/classes'

directory STAGING_CLS_MAIN_DIR   = STAGING_CLS_DIR + '/main'
directory STAGING_CLS_TEST_DIR   = STAGING_CLS_DIR + '/test'
directory STAGING_CLS_JRUBY_DIR  = STAGING_CLS_DIR + '/jruby'

directory STAGING_REPORT_DIR     = STAGING_DIR + '/report'

# this is the destination of the Java-only jarfile:
directory STAGING_LIBS           = STAGING_DIR + '/libs'

directory STAGING_DIST_DIR       = STAGING_DIR + "/dist"
directory STAGING_DIST_DIFFJ_DIR = STAGING_DIST_DIR + "/#{DIFFJ_FULLNAME}"

directory STAGING_DIST_BIN_DIR   = STAGING_DIST_DIFFJ_DIR + '/bin'
directory STAGING_DIST_LIB_DIR   = STAGING_DIST_DIFFJ_DIR + '/lib/' + DIFFJ_NAME

LIBS_DIR           = 'libs'
JRUBY_COMPLETE_JAR = 'libs/jruby-complete-1.6.3.jar'
PMD_JAR            = 'libs/pmd-4.2.5.jar'
JUNIT_JAR          = 'libs/junit-4.10.jar'

# we're still using this, for JRuby vs. Java tests:
DIFFJ_JAVA_JAR     = "staging/libs/#{DIFFJ_FULLNAME}.jar"

# this is the full JRuby jarfile, which will replace the above:
DIFFJ_JRUBY_JAR    = "#{DIFFJ_FULLNAME}.jar"

# this is fixed in JRuby 1.6.0:
$CLASSPATH << "#{ENV['JAVA_HOME']}/lib/tools.jar"

# This doesn't seem to work. If DIFFJ_JAVA_JAR doesn't exist when the Rakefile
# is executed, java:jar is executed, but the jruby:tests task fails with an
# error that the DiffJ Java code can't be found. But the next time jruby:tests
# runs (with the diffj jarfile existing now), it runs successfully.

$CLASSPATH << DIFFJ_JAVA_JAR
$CLASSPATH << JRUBY_COMPLETE_JAR
$CLASSPATH << PMD_JAR

buildjars = [ JRUBY_COMPLETE_JAR, PMD_JAR ]
testjars =  [ JUNIT_JAR ]

# Ant code to build Java

task :setup do
  ant.path :id => 'classpath' do
    buildjars.each do |jarfile|
      fileset :file => jarfile
    end
  end

  ant.path :id => 'test.classpath' do
    pathelement :location => STAGING_CLS_MAIN_DIR
    path        :refid    => 'classpath'
    testjars.each do |jarfile|
      fileset :file => jarfile
    end
    pathelement :location => STAGING_CLS_TEST_DIR
  end
end

task 'java:compile' => [ :setup, STAGING_CLS_MAIN_DIR ] do
  ant.javac(:destdir => STAGING_CLS_MAIN_DIR, 
            :srcdir => SRC_MAIN_JAVA_DIR,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task 'java:tests:compile' => [ :setup, STAGING_CLS_TEST_DIR, 'java:compile' ] do
  ant.javac(:destdir => STAGING_CLS_TEST_DIR, 
            :srcdir => SRC_TEST_JAVA_DIR,
            :classpathref => 'test.classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

# this should depend on the tests, but sometimes I create a jar for
# field testing, which won't yet pass the tests.
desc "Build the jarfile for the Java code (no JRuby)"
task "java:jar" => [ "java:compile", STAGING_LIBS ] do
  ant.jar(:jarfile => DIFFJ_JAVA_JAR, 
          :basedir => STAGING_CLS_MAIN_DIR)
end

desc "Run the Java tests"
task "java:tests" => [ "java:tests:compile", STAGING_REPORT_DIR ] do  
  ant.junit(:fork => "yes", :forkmode => "once", :printsummary => "yes",  
            :showoutput => true,
            :haltonfailure => "no", :failureproperty => "tests.failed") do  
    classpath :refid => 'test.classpath'  
    formatter :type => "xml"
    formatter :type => "plain"
    batchtest :todir => STAGING_REPORT_DIR do  
      fileset :dir => SRC_TEST_JAVA_DIR, :includes => '**/Test*.java'  
    end  
  end  
  if ant.project.getProperty "tests.failed"
    ant.junitreport :todir => STAGING_REPORT_DIR do  
      fileset :dir => STAGING_REPORT_DIR, :includes => "TEST-*.xml"  
      report :todir => "#{STAGING_REPORT_DIR}/html"  
    end  
    ant.fail :message => "Test(s) failed. Report is at #{STAGING_REPORT_DIR}/html."
  end  
end

# JRuby tasks:

desc "Compile the JRuby code"
task "jruby:compile" => [ :setup, STAGING_CLS_MAIN_DIR ] do
  ant.javac(:destdir => STAGING_CLS_MAIN_DIR, 
            :srcdir => SRC_MAIN_JRUBY_DIR,
            :classpathref => 'classpath',
            :debug => 'yes',
            :includeantruntime => 'no')
end

desc "Build the jarfile including JRuby and PMD"
task "jruby:jar" => [ "java:compile", "jruby:compile" ] do
  cmd  = "jar -cfm #{DIFFJ_JRUBY_JAR} src/main/jar/launcher.manifest "
  cmd << "-C #{STAGING_CLS_MAIN_DIR} org/incava/diffj/DiffJLauncher.class "
  # this is PMD and JRuby combined, since jar whines about duplicate directories (such as "org"):
  cmd << "-C vendor/all . "
  cmd << "-C #{SRC_MAIN_RUBY_DIR} . "
  sh cmd
end

class DiffJRakeTestTask < Rake::TestTask
  def initialize name, filter = name
    super(('test:' + name) => [ "java:tests:compile", "java:jar" ]) do |t|
      t.libs << SRC_MAIN_RUBY_DIR
      t.libs << SRC_TEST_RUBY_DIR
      t.pattern = "#{SRC_TEST_RUBY_DIR}/**/#{filter}/**/test*.rb"
      t.warning = true
      t.verbose = true
    end
  end
end

DiffJRakeTestTask.new 'all', '*'
DiffJRakeTestTask.new 'imports'
DiffJRakeTestTask.new 'ctor'
DiffJRakeTestTask.new 'field'
DiffJRakeTestTask.new 'method'
DiffJRakeTestTask.new 'type'
DiffJRakeTestTask.new 'types'
DiffJRakeTestTask.new 'method/body/zeroone'
DiffJRakeTestTask.new 'method/parameters/zeroone'
DiffJRakeTestTask.new 'method/throws/zeroone'
DiffJRakeTestTask.new 'method/parameters/reorder'
DiffJRakeTestTask.new 'method/parameters/reorder/typechange'

task "jruby:tests" => [ "test:all" ]

desc "Distribution"
task "dist" => [ "java:jar", "jruby:jar", STAGING_DIST_BIN_DIR, STAGING_DIST_LIB_DIR ] do
  cp "src/main/sh/diffj", STAGING_DIST_BIN_DIR
  cp DIFFJ_JRUBY_JAR, STAGING_DIST_LIB_DIR
  origdir = Dir.pwd
  cd STAGING_DIST_DIR
  sh "zip -r #{DIFFJ_FULLNAME}.zip #{DIFFJ_FULLNAME}"
  sh "tar zcvf #{DIFFJ_FULLNAME}.tar.gz #{DIFFJ_FULLNAME}"
  cd origdir
end

desc "Build Debian package"
task "debian:dist" => [ "dist" ] do
  cd STAGING_DIST_DIR + '/' + DIFFJ_FULLNAME
  debpkgfile = "../diffj_#{DIFFJ_VERSION}_all.deb"
  if File.exists? debpkgfile
    rm debpkgfile
  end
  cmd = Array.new
  cmd << "fpm"
  cmd << "-s" << "dir"
  cmd << "-t" << "deb"
  cmd << "--name" << "diffj"
  cmd << "--version" << DIFFJ_VERSION
  cmd << "--prefix" << "usr"
  cmd << "--architecture" << "all"
  cmd << "--maintainer" << "'jeugenepace at gmail dot com'"
  cmd << "--description" << "'Java-aware file comparator'"
  cmd << "--url" << "http://www.incava.org/projects/diffj"
  cmd << "--package" << debpkgfile
  cmd << "."
  sh cmd.join(' ')
end

task "clean" do
  staging = Pathname.new "staging"
  staging.rmtree if staging.exist?
end

# todo:

# add gem install riel
# dependencies from Rake on Gradle
