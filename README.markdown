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

## Deployment (legacy 4/17/2024)

There is a bash script to handle deployment. You can run it like this:

    ./deploy development

You can also specify acceptance or production environments. Note that it is not
git-aware. It will work with whatever files you have in your local working
tree. It simply compiles the ClojureScript and syncs html, js, and css to the
appropriate server. There currently is no rollback. You can handle that locally
by checking out a previous git revision and re-running.

## Development

Open [development.html](development.html) in your browser (Chrome recommended,
because it works better with [source
maps](https://github.com/clojure/clojurescript/wiki/Source-maps)).

You can access the development version of the reporter by setting up a basic Web server:

    python -m SimpleHTTPServer 8000

Then going to:

    localhost:8000/development.html?uri=<some_uri>

### Set up your editor

Here are some helpful hacks for your vim setup. (If in Emacs, no worries,
you're already in Lisp land. And in any case, I can't help you.)

Gabe Hollombe has a good [blog post on vim and
Clojure](http://www.neo.com/2014/02/25/getting-started-with-clojure-in-vim).

Personally, I like the combination of tmux and Vimux, with the ability to send
chunks of code over to a repl running in a separate pane. I will probably write
up a blog post on the process sometime.

### Run the Tests

The tests require [Firefox, version
28](http://ftp.mozilla.org/pub/mozilla.org/firefox/releases/28.0/win32/en-US/).
To run a newer Firefox, update the selenium-webdriver gem.

The tests also require Ruby (~ 2.1) and that you are compiling your ClojureScript.

    bundle install
    lein cljsbuild auto development
    bundle exec rspec spec/

The tests currently hit live data, so it is possible that this will cause
intermittent failures.

## Using nixpkgs

If you have nixpkgs install with
[flakes enabled](https://nixos.wiki/wiki/Flakes), you can run the following
command to start a shell with java 8 and lein in the path:

```bash
$ nix develop
```

If you have nixpkgs setup with direnv, you can run the following to create
a `.envrc` file and if you allow it, it will load the environment.

```bash
$ echo 'use flake' > .envrc
...
$ direnv allow
```

## Generate Production Static Files

To generate production files that get put in the "out" directorry in this
project, run the following command:

```bash
$ docker-compose up generate-production
```

If you are using podman, it can use the same `docker-compose.yml` file
with this command:

```bash
$ podman compose up generate-production
```
