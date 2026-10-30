package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.Classificado;
import com.eocape.backend.classificado.StatusClassificado;
import com.eocape.backend.classificado.TipoClassificado;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClassificadoResponse(
        UUID id,
        UUID condominioId,
        UUID usuarioId,
        String usuarioNome,
        String titulo,
        String descricao,
        TipoClassificado tipo,
        BigDecimal preco,
        StatusClassificado status,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ClassificadoResponse from(Classificado c) {
        return new ClassificadoResponse(
                c.getId(),
                c.getCondominio().getId(),
                c.getUsuario().getId(),
                c.getUsuario().getNome(),
                c.getTitulo(),
                c.getDescricao(),
                c.getTipo(),
                c.getPreco(),
                c.getStatus(),
                c.getCriadoEm(),
                c.getAtualizadoEm());
    }
}