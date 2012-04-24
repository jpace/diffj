require 'rubygems'
require 'rake'
require 'java'
require 'rake/testtask'

include Java

require 'ant'

$CLASSPATH << ":/usr/lib/jvm/java-6-openjdk/lib/tools.jar"
$CLASSPATH << "build/libs/diffj-1.2.1.jar"
$CLASSPATH << "libs/jruby-complete-1.6.3.jar"
$CLASSPATH << "libs/pmd-4.2.5.jar"

puts "ant: #{ant}"

task :setup do
  puts "ant: #{ant}"
  ant.path :id => 'classpath' do
    fileset :dir => 'staging/classes'
    fileset :file => 'libs/jruby-complete-1.6.3.jar'
    fileset :file => 'libs/junit-4.10.jar'
    fileset :file => 'libs/pmd-4.2.5.jar'
  end
end

directory 'staging/classes'
directory 'staging/libs'

puts "classpath: #{$CLASSPATH}"

task :compile => [ :setup, 'staging/classes' ] do
  ant.javac(:destdir => 'staging/classes', 
            :srcdir => 'src/main/java',
            :classpathref => 'classpath',
            :source => '1.6',
            :target => '1.6',
            :debug => 'yes',
            :includeantruntime => 'no')
end

task :jar => [ :compile, 'staging/libs' ] do
  ant.jar(:jarfile => 'staging/libs/diffj-1.2.1.jar', 
          :basedir => 'staging/classes')
end

class DiffJRakeTestTask < Rake::TestTask
  def initialize name, filter = name
    super('test:' + name) do |t|
      t.options = { :needs => [ :something ] }
      t.libs << "src/main/ruby"
      t.libs << "src/test/ruby"
      # t.libs << "staging/libs/diffj-1.2.1.jar"
      # t.libs << "libs/jruby-complete-1.6.3.jar"
      # t.libs << "libs/pmd-4.2.5.jar" # this gets mushed into diffj-1.2.1.jar, but for future builds it won't.
      t.pattern = "src/test/ruby/**/#{filter}/**/test*.rb"
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
