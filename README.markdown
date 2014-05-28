This is a tool to generate date-delimited reports for individual scholars in a
[Vivo](http://www.vivoweb.org/) instance. The data is obtained through a web
service ([Vivo Widgets](https://git.oit.duke.edu/vivo_widgets)), rather than
directly from a SPARQL endpoint.

This project uses [Om](https://github.com/swannodette/om), a
[Clojurescript](https://github.com/clojure/clojurescript) wrapper for the
Javascript framework [React](http://facebook.github.io/react/index.html). It
produces static html, css and javascript to do the reporting.

## Setup

Install [Leiningen](http://leiningen.org). If you are using Homebrew on a Mac,
you can run:

    brew install leiningen

Compile the files:

    git clone https://github.com/OIT-ADS-Web/vivo_widgets_reporter.git
    cd vivo_widgets_reporter
    lein cljsbuild auto development

This will continually compile the files each time you save one. If you just
want to compile once, run:

    lein cljsbuild once development

## Development

Open [development.html](development.html) in your browser (Chrome recommended,
because it works better with [source
maps](https://github.com/clojure/clojurescript/wiki/Source-maps)).

[Something about Om and React]

### Set up your editor

Here are some helpful hacks for your vim setup. (If in Emacs, no worries,
you're already in Lisp land. And in any case, I can't help you.)

Gabe Hollombe has a good [blog post on vim and
Clojure](http://www.neo.com/2014/02/25/getting-started-with-clojure-in-vim).

Personally, I like the combination of tmux and Vimux, with the ability to send
chunks of code over to a repl running in a separate pane. I will probably write
up a blog post on the process sometime.

### Run the Tests

The tests require [PhantomJS](http://phantomjs.org). To install with Homebrew:

    brew update && brew install phantomjs

To run from the base directory:

    lein test
