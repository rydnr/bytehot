{
  description = "Flake for https://github.com/rydnr/bytehot";

 nixConfig.permittedInsecurePackages = [ "openssl-1.1.1w" ];

 inputs = {
    flake-utils.url = "github:numtide/flake-utils/v1.0.0";
    nixpkgs.url = "github:NixOS/nixpkgs/release-25.05";
  };
  outputs = inputs:
    with inputs;
    flake-utils.lib.eachDefaultSystem (system:
      let
        pname = "rydnr-bytehot";
        officialNixpkgs = import nixpkgs {
          inherit system;
        };
        pkgs-for = { jdk }:
          import nixpkgs {
            inherit system;
            config.allowInsecure = true;
            overlays = [
              (final: prev: {
                mavenFullJdk = prev.maven.override {
                  jdk_headless = jdk;
                };
              })
            ];
          };
        nixpkgsVersion = builtins.readFile "${nixpkgs}/.version";
        nixpkgsRelease =
          builtins.replaceStrings [ "\n" ] [ "" ] "nixpkgs-${nixpkgsVersion}";
        shared = import ./shared.nix;
        description = "TODO";
        homepage = "https://github.com/rydnr/bytehot";
        rydnr-bytehot-for = { jdk, pkgs }:
          pkgs.stdenv.mkDerivation {
            inherit pname;
            version = "latest-SNAPSHOT";

            src = ./..;

            nativeBuildInputs = [ jdk pkgs.mavenFullJdk pkgs.curl pkgs.openssl ];

            buildPhase = ''
                # don't try to compile for now since it requires declaring its dependencies beforehand.
                # mvn compile
            '';

            installPhase = ''
                mkdir $out
                # don't try to install for now since it requires declaring its dependencies beforehand.
                # mvn install
                  '';
            meta = {
              inherit description homepage;
            };
          };
      in rec {
        defaultPackage = packages.default;
        devShells = rec {
          default = rydnr-bytehot-8;
          rydnr-bytehot-23 = shared.devShell-for rec {
            jdk = pkgs.openjdk23;
            maven = pkgs.mavenFullJdk;
            nativeDeps = [ pkgs.curl.out pkgs.openssl_1_1.out ];
            inherit nixpkgsRelease;
            package = packages.rydnr-bytehot-23;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk23; };
          };
          rydnr-bytehot-21 = shared.devShell-for rec {
            jdk = pkgs.openjdk21;
            maven = pkgs.mavenFullJdk;
            nativeDeps = [ pkgs.curl.out pkgs.openssl_1_1.out ];
            inherit nixpkgsRelease;
            package = packages.rydnr-bytehot-21;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk21; };
          };
          rydnr-bytehot-17 = shared.devShell-for rec {
            jdk = pkgs.openjdk17;
            maven = pkgs.mavenFullJdk;
            nativeDeps = [ pkgs.curl.out pkgs.openssl_1_1.out ];
            inherit nixpkgsRelease;
            package = packages.rydnr-bytehot-17;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk17; };
          };
          rydnr-bytehot-11 = shared.devShell-for rec {
            jdk = pkgs.openjdk11;
            maven = pkgs.mavenFullJdk;
            nativeDeps = [ pkgs.curl.out pkgs.openssl_1_1.out ];
            inherit nixpkgsRelease;
            package = packages.rydnr-bytehot-11;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk11; };
          };
          rydnr-bytehot-8 = shared.devShell-for rec {
            jdk = pkgs.openjdk8;
            maven = pkgs.mavenFullJdk;
            nativeDeps = [ pkgs.curl.out pkgs.openssl_1_1.out ];
            inherit nixpkgsRelease;
            package = packages.rydnr-bytehot-8;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk8; };
          };
        };
        packages = rec {
          default = rydnr-bytehot-8;
          rydnr-bytehot-23 = rydnr-bytehot-for rec {
            jdk = pkgs.openjdk23;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk23; };
          };
          rydnr-bytehot-21 = rydnr-bytehot-for rec {
            jdk = pkgs.openjdk21;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk21; };
          };
          rydnr-bytehot-17 = rydnr-bytehot-for rec {
            jdk = pkgs.openjdk17;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk17; };
          };
          rydnr-bytehot-11 = rydnr-bytehot-for rec {
            jdk = pkgs.openjdk11;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk11; };
          };
          rydnr-bytehot-8 = rydnr-bytehot-for rec {
            jdk = pkgs.openjdk8;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk8; };
          };
        };
      });
}
