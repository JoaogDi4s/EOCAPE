package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoManutencao;

import java.util.UUID;

public record ArquivoManutencaoResponse(
        UUID id,
        UUID manutencaoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    public static ArquivoManutencaoResponse from(ArquivoManutencao vinculo) {
        return new ArquivoManutencaoResponse(
                vinculo.getId(),
                vinculo.getManutencao().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}