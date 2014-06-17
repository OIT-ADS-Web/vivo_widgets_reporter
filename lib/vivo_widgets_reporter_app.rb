class VivoWidgetsReporterApp
  def self.call(*args)
    [200,
     {'Content-Type' => 'text/html', 'Cache-Control' => 'public, max-age=86400'},
     File.open('development.html', File::RDONLY)]
  end
end
