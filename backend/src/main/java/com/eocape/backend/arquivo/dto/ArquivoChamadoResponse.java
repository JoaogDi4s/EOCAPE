package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoChamado;

import java.util.UUID;

public record ArquivoChamadoResponse(
        UUID id,
        UUID chamadoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    public static ArquivoChamadoResponse from(ArquivoChamado vinculo) {
        return new ArquivoChamadoResponse(
                vinculo.getId(),
                vinculo.getChamado().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}