package com.eocape.backend.vinculoUnidade.dto;

import com.eocape.backend.vinculoUnidade.VinculoUnidade;

import java.util.UUID;

public record VinculoUnidadeResponse(
        UUID id,
        UUID usuarioId,
        UUID unidadeId,
        String tipoVinculo,
        boolean principal,
        boolean ativo) {

    public static VinculoUnidadeResponse from(VinculoUnidade vinculo) {
        return new VinculoUnidadeResponse(
                vinculo.getId(),
                vinculo.getUsuario().getId(),
                vinculo.getUnidade().getId(),
                vinculo.getTipoVinculo(),
                vinculo.isPrincipal(),
                vinculo.isAtivo());
    }
}