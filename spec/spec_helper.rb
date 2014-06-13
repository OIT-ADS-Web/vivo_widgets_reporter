require 'capybara/rspec'
require 'vivo_widgets_reporter_app'

Capybara.app = Rack::Builder.new do |env|
  use Rack::Static,
    :urls => ["/assets/js", "/assets/css", "/out/development", "/out/test"]
  map '/' do
    run VivoWidgetsReporterApp
  end
end

RSpec.configure do |config|
  config.before(:suite) do
    system("lein cljsbuild once development")
  end
end
