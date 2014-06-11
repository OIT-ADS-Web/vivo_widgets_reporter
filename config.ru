require 'vivo_widgets_reporter_app'
use Rack::Static,
  :urls => ["/assets/js", "/assets/css", "/out/development", "/out/test"]
run VivoWidgetsReporterApp
