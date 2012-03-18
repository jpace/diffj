def jrtest fname
  task taskname do |t|
    sh "jruby #{fname}"
  end
end
