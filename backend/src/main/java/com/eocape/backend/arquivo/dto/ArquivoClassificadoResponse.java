package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoClassificado;

import java.util.UUID;

public record ArquivoClassificadoResponse(
        UUID id,
        UUID classificadoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    public static ArquivoClassificadoResponse from(ArquivoClassificado vinculo) {
        return new ArquivoClassificadoResponse(
                vinculo.getId(),
                vinculo.getClassificado().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}