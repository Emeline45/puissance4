{ pkgs ? import <nixpkgs> {}
}:

pkgs.mkShell {
  name = "ia-shell";
  version = "0.0.1";

  buildInputs = with pkgs; [
    jetbrains.idea-community
    jdk11
    maven
  ];

  JAVA_HOME = "${pkgs.jdk11}/lib/openjdk";
}
