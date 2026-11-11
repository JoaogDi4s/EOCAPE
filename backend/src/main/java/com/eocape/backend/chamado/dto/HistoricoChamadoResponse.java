package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.HistoricoChamado;
import com.eocape.backend.chamado.StatusChamado;

import java.time.Instant;
import java.util.UUID;

public record HistoricoChamadoResponse(
        UUID id,
        UUID chamadoId,
        UUID usuarioId,
        String usuarioNome,
        StatusChamado statusAnterior,
        StatusChamado statusNovo,
        String comentario,
        Instant criadoEm
) {
    public static HistoricoChamadoResponse from(HistoricoChamado h) {
        return new HistoricoChamadoResponse(
                h.getId(),
                h.getChamado().getId(),
                h.getUsuario().getId(),
                h.getUsuario().getNome(),
                h.getStatusAnterior(),
                h.getStatusNovo(),
                h.getComentario(),
                h.getCriadoEm());
    }
}