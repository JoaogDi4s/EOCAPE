package com.eocape.backend.bloco.dto;

import com.eocape.backend.bloco.Bloco;

import java.time.Instant;
import java.util.UUID;

public record BlocoResponse(
        UUID id,
        String nome,
        UUID condominioId,
        Instant criadoEm) {
    public static BlocoResponse from(Bloco bloco) {
        return new BlocoResponse(
                bloco.getId(),
                bloco.getNome(),
                bloco.getCondominio().getId(),
                bloco.getCriadoEm());
    }
}