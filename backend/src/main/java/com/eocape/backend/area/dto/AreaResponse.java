package com.eocape.backend.area.dto;

import com.eocape.backend.area.Area;

import java.time.Instant;
import java.util.UUID;

public record AreaResponse(
        UUID id,
        String nome,
        UUID condominioId,
        String descricao,
        boolean ativo,
        Instant criadoEm) {
    public static AreaResponse from(Area area) {
        return new AreaResponse(
                area.getId(),
                area.getNome(),
                area.getCondominio().getId(),
                area.getDescricao(),
                area.isAtivo(),
                area.getCriadoEm());
    }
}