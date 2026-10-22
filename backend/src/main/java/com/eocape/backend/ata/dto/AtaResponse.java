package com.eocape.backend.ata.dto;

import com.eocape.backend.ata.Ata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AtaResponse(
        UUID id,
        String titulo,
        LocalDate dataReuniao,
        String resumo,
        boolean publicada,
        String criadoPorNome,
        Instant criadoEm
) {
    public static AtaResponse from(Ata ata) {
        return new AtaResponse(
                ata.getId(),
                ata.getTitulo(),
                ata.getDataReuniao(),
                ata.getResumo(),
                ata.isPublicada(),
                ata.getCriadoPor().getNome(),
                ata.getCriadoEm()
        );
    }
}