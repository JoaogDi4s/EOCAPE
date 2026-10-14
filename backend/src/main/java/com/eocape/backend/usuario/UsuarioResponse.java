package com.eocape.backend.usuario;

import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String telefone,
    boolean primeiroAcessoConcluido
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.isPrimeiroAcessoConcluido()
        );
    }
}