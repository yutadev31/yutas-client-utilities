{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs =
    {
      self,
      nixpkgs,
    }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        nativeBuildInputs = with pkgs; [
          jdk21
        ];

        buildInputs = with pkgs; [
          libGL
          mesa
          glfw
          xorg.libX11
          xorg.libXext
          xorg.libXrandr
          xorg.libXcursor
          xorg.libXi
          xorg.libXinerama
        ];

        LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [
          pkgs.libGL
          pkgs.mesa
          pkgs.glfw
          pkgs.xorg.libX11
          pkgs.xorg.libXext
          pkgs.xorg.libXrandr
          pkgs.xorg.libXcursor
          pkgs.xorg.libXi
          pkgs.xorg.libXinerama
        ];
      };
    };
}
