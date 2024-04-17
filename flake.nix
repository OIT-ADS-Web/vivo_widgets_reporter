{
  inputs = { 
    nixpkgs.url = "github:nixos/nixpkgs/23.11";
    utils.url = "github:numtide/flake-utils"; 
  };
  outputs = { self, nixpkgs, utils }:
    utils.lib.eachDefaultSystem (system:
      let
        jdk = pkgs.jdk8;
        overlays = [
          (final: prev: {
            leiningen = prev.leiningen.override { jdk = final.jdk8; };
          })
        ];
        pkgs = import nixpkgs {
          overlays = overlays;
          system = system;
        };

      in {
        overlays.default = overlays;
        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk
            leiningen
          ];
        };
      });
}
