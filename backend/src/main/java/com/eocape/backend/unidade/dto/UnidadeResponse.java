package com.eocape.backend.unidade.dto;

import com.eocape.backend.unidade.Unidade;

import java.time.Instant;
import java.util.UUID;

public record UnidadeResponse(
        UUID id,
        String numero,
        Integer andar,
        UUID blocoId,
        Instant criadoEm
) {
    public static UnidadeResponse from(Unidade unidade) {
        return new UnidadeResponse(
                unidade.getId(),
                unidade.getNumero(),
                unidade.getAndar(),
                unidade.getBloco().getId(),
                unidade.getCriadoEm()
        );
    }
}