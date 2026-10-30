package com.eocape.backend.agendamentoEspaco.dto;

import com.eocape.backend.agendamentoEspaco.AgendamentoEspaco;
import com.eocape.backend.agendamentoEspaco.StatusAgendamento;

import java.time.Instant;
import java.util.UUID;

public record AgendamentoEspacoResponse(
        UUID id,
        UUID condominioId,
        UUID espacoComumId,
        String areaNome,
        UUID usuarioId,
        String usuarioNome,
        Instant inicio,
        Instant fim,
        StatusAgendamento status,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static AgendamentoEspacoResponse from(AgendamentoEspaco a) {
        return new AgendamentoEspacoResponse(
                a.getId(),
                a.getCondominio().getId(),
                a.getEspacoComum().getId(),
                a.getEspacoComum().getArea().getNome(),
                a.getUsuario().getId(),
                a.getUsuario().getNome(),
                a.getInicio(),
                a.getFim(),
                a.getStatus(),
                a.getObservacao(),
                a.getCriadoEm(),
                a.getAtualizadoEm());
    }
}