package com.eocape.backend.usuario;

public record LoginResponse(
    String token,
    boolean trocaSenhaObrigatoria
) {}