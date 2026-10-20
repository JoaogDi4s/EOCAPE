package com.eocape.backend.aviso.dto.response;

import com.eocape.backend.aviso.Aviso;

import java.time.Instant;
import java.util.UUID;

public record AvisoResponse(
        UUID id,
        UUID condominioId,
        UUID blocoId,
        UUID autorId,
        String autorNome,
        String titulo,
        String conteudo,
        boolean importante,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static AvisoResponse from(Aviso aviso) {
        return new AvisoResponse(
                aviso.getId(),
                aviso.getCondominio().getId(),
                aviso.getBloco().getId(),
                aviso.getAutor().getId(),
                aviso.getAutor().getNome(),
                aviso.getTitulo(),
                aviso.getConteudo(),
                aviso.isImportante(),
                aviso.getCriadoEm(),
                aviso.getAtualizadoEm()
        );
    }
}