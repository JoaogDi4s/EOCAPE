package com.eocape.backend.manutencao.dto;

import com.eocape.backend.manutencao.Manutencao;
import com.eocape.backend.manutencao.StatusManutencao;
import com.eocape.backend.manutencao.TipoManutencao;

import java.util.UUID;

public record ManutencaoResponse(
        UUID id,
        UUID condominioId,
        UUID unidadeId,
        UUID areaId,
        UUID chamadoId,
        UUID criadoPorId,
        String criadoPorNome,
        String titulo,
        String descricao,
        TipoManutencao tipo,
        StatusManutencao status
) {
    public static ManutencaoResponse from(Manutencao m) {
        return new ManutencaoResponse(
                m.getId(),
                m.getCondominio().getId(),
                m.getUnidade() != null ? m.getUnidade().getId() : null,
                m.getArea() != null ? m.getArea().getId() : null,
                m.getChamado() != null ? m.getChamado().getId() : null,
                m.getCriadoPor().getId(),
                m.getCriadoPor().getNome(),
                m.getTitulo(),
                m.getDescricao(),
                m.getTipo(),
                m.getStatus());
    }
}