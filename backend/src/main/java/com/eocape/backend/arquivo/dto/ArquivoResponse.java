package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.Arquivo;

import java.time.Instant;
import java.util.UUID;

public record ArquivoResponse(
        UUID id,
        UUID condominioId,
        UUID enviadoPorId,
        String enviadoPorNome,
        String nomeOriginal,
        String chaveArmazenamento,
        String tipoMime,
        Instant criadoEm
) {
    public static ArquivoResponse from(Arquivo a) {
        return new ArquivoResponse(
                a.getId(),
                a.getCondominio().getId(),
                a.getEnviadoPor().getId(),
                a.getEnviadoPor().getNome(),
                a.getNomeOriginal(),
                a.getChaveArmazenamento(),
                a.getTipoMime(),
                a.getCriadoEm());
    }
}