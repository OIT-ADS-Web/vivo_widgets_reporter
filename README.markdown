## Setup

Install [Leiningen](http://leiningen.org). If you are using Homebrew on a Mac, you can run:

    brew install leiningen

Compile the files:

    git clone https://github.com/OIT-ADS-Web/vivo_widgets_reporter.git
    cd vivo_widgets_reporter
    lein cljsbuild auto development

## Development

### Run the Tests

To run from the base directory:

    lein test

The tests require [PhantomJS](http://phantomjs.org). To install with Homebrew:

    brew update && brew install phantomjs
