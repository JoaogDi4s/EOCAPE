package com.eocape.backend.usuario.dto.response;

public record LoginResponse(
    String token,
    boolean trocaSenhaObrigatoria
) {}