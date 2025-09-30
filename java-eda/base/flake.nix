{
  description = "Flake for https://github.com/java-eda/base";

  inputs = {
    flake-utils.url = "github:numtide/flake-utils/v1.0.0";
    nixpkgs.url = "github:NixOS/nixpkgs/release-25.05";
  };

  outputs = inputs:
    with inputs;
    flake-utils.lib.eachDefaultSystem (system:
      let
        pname = "java-eda-base";
        officialNixpkgs = import nixpkgs { inherit system; };
        pkgs-for = { jdk }:
          import nixpkgs {
            inherit system;
            overlays = [
              (final: prev: {
                mavenFullJdk = prev.maven.override { jdk_headless = jdk; };
              })
            ];
          };
        nixpkgsVersion = builtins.readFile "${nixpkgs}/.version";
        nixpkgsRelease =
          builtins.replaceStrings [ "\n" ] [ "" ] "nixpkgs-${nixpkgsVersion}";
        shared = import ./shared.nix;
        description = "Base POM for EDA applications.";
        homepage = "https://github.com/java-eda/base";
        java-eda-base-for = { jdk, pkgs }:
          pkgs.stdenv.mkDerivation {
            inherit pname;
            version = "0.0.0-SNAPSHOT";

            # The source can be the local directory or elsewhere.
            src = ./..;

            # This ensures our build uses openjdk21.
            nativeBuildInputs = [ jdk pkgs.mavenFullJdk ];

            # Simple example: compile a single Main.java file.
            buildPhase = ''
              # don't try to compile for now since it requires declaring its dependencies beforehand.
              # mvn compile
            '';

            installPhase = ''
              mkdir $out
              # don't try to install for now since it requires declaring its dependencies beforehand.
              # mvn install
            '';
            meta = { inherit description homepage; };
          };
      in rec {
        defaultPackage = packages.default;
        devShells = rec {
          default = java-eda-base-21;
          java-eda-base-23 = shared.devShell-for rec {
            jdk = pkgs.openjdk23;
            maven = pkgs.mavenFullJdk;
            inherit nixpkgsRelease;
            package = packages.java-eda-base-23;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk23; };
          };
          java-eda-base-21 = shared.devShell-for rec {
            jdk = pkgs.openjdk21;
            maven = pkgs.mavenFullJdk;
            inherit nixpkgsRelease;
            package = packages.java-eda-base-21;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk21; };
          };
          java-eda-base-17 = shared.devShell-for rec {
            jdk = pkgs.openjdk17;
            maven = pkgs.mavenFullJdk;
            inherit nixpkgsRelease;
            package = packages.java-eda-base-17;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk17; };
          };
          java-eda-base-11 = shared.devShell-for rec {
            jdk = pkgs.openjdk11;
            maven = pkgs.mavenFullJdk;
            inherit nixpkgsRelease;
            package = packages.java-eda-base-11;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk11; };
          };
        };
        packages = rec {
          default = java-eda-base-21;
          java-eda-base-23 = java-eda-base-for rec {
            jdk = pkgs.openjdk23;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk23; };
          };
          java-eda-base-21 = java-eda-base-for rec {
            jdk = pkgs.openjdk21;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk21; };
          };
          java-eda-base-17 = java-eda-base-for rec {
            jdk = pkgs.openjdk17;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk17; };
          };
          java-eda-base-11 = java-eda-base-for rec {
            jdk = pkgs.openjdk11;
            pkgs = pkgs-for { jdk = officialNixpkgs.openjdk11; };
          };
        };
      });
}
