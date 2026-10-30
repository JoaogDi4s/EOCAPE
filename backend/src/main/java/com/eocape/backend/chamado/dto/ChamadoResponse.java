package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.chamado.StatusChamado;

import java.time.Instant;
import java.util.UUID;

public record ChamadoResponse(
        UUID id,
        UUID condominioId,
        UUID usuarioId,
        String usuarioNome,
        UUID unidadeId,
        String unidadeNumero,
        UUID areaId,
        String titulo,
        String descricao,
        String categoria,
        StatusChamado status,
        Instant concluidoEm,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ChamadoResponse from(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getCondominio().getId(),
                chamado.getUsuario().getId(),
                chamado.getUsuario().getNome(),
                chamado.getUnidade().getId(),
                chamado.getUnidade().getNumero(),
                chamado.getArea() != null ? chamado.getArea().getId() : null,
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getCategoria(),
                chamado.getStatus(),
                chamado.getConcluidoEm(),
                chamado.getCriadoEm(),
                chamado.getAtualizadoEm());
    }
}