def buildfile fname
  File.join $builddir, fname
end

def copytask fname, deps, taskname
  tgtfile = buildfile(fname)
  puts "copytask: #{fname} #{tgtfile}"
  file tgtfile => deps do |t|
    puts "cp: #{t.prerequisites.last} #{t.name}"
    cp t.prerequisites.last, t.name
  end
  task taskname => tgtfile
end

def jrubyctask rbfname, taskname
  task taskname do |t|
    sh "jrubyc -t #{$builddir} --javac #{rbfname}"
  end
end

def copygroup files, taskname
  files.each do |file|
    tgtfile = buildfile file
    file tgtfile => file do |t|
      cp file, tgtfile
    end
    task taskname => tgtfile
  end
end
