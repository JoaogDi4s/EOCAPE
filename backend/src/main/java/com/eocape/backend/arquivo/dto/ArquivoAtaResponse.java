package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoAta;

import java.util.UUID;

public record ArquivoAtaResponse(
        UUID id,
        UUID ataId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    public static ArquivoAtaResponse from(ArquivoAta vinculo) {
        return new ArquivoAtaResponse(
                vinculo.getId(),
                vinculo.getAta().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}