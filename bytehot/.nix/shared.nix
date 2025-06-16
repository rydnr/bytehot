rec {
  shellHook-for = { jdk, maven, nativeDeps, nixpkgsRelease, package, patchelf }: ''
    export PNAME="${package.pname}";
    export PVERSION="${package.version}";
    export JDKVERSION="${jdk.name}";
    export MAVENVERSION="${maven.name}";
    export NIXPKGSRELEASE="${nixpkgsRelease}";
    export NATIVEDEPS="${builtins.concatStringsSep " " nativeDeps}";
    export PATH="${maven}/bin:${jdk}/bin:$PATH";
    export PS1="\033[37m[\[\033[01;33m\]\$PNAME-\$PVERSION\033[01;37m|\033[01;32m\]\$JDKVERSION\]\033[37m|\[\033[00m\]\[\033[01;34m\]\W\033[37m]\033[31m\$\[\033[00m\] ";
    bs='\\'
    echo;
    echo;
    echo -e "\033[32m$PNAME-$PVERSION\033[0m on \033[34m$JDKVERSION, \033[35m$MAVENVERSION\033[0";
    echo;
    echo -e "\033[0mNative deps:\033[0m";
    for dep in $NATIVEDEPS; do
      echo -e "\033[0m - \033[31m$dep\033[0m";
      export LD_LIBRARY_PATH=$dep/lib:$LD_LIBRARY_PATH
    done;
    export JAVA_HOME="${jdk}";
    echo
  '';
  devShell-for = { jdk, maven, nativeDeps, nixpkgsRelease, package, pkgs }:
    pkgs.mkShell {
      buildInputs = [ package ];
      shellHook = shellHook-for { patchelf = pkgs.patchelf; inherit jdk maven nativeDeps nixpkgsRelease package ; };
    };
  app-for = { entrypoint, jdk, package }: {
    type = "app";
    program = "${jdk}/bin/java -jar ${package}/target/${entrypoint}";
  };
}
