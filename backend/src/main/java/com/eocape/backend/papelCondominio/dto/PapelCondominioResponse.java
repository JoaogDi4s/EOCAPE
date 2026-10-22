package com.eocape.backend.papelCondominio.dto;

import java.time.Instant;
import java.util.UUID;

import com.eocape.backend.papelCondominio.PapelCondominio;

public record PapelCondominioResponse(
        UUID id,
        UUID usuarioId,
        UUID condominioId,
        String papel,
        boolean ativo,
        Instant criadoEm) {

    public static PapelCondominioResponse from(PapelCondominio papelCondominio) {
        return new PapelCondominioResponse(
                papelCondominio.getId(),
                papelCondominio.getUsuario().getId(),
                papelCondominio.getCondominio().getId(),
                papelCondominio.getPapel(),
                papelCondominio.isAtivo(),
                papelCondominio.getCriadoEm());
    }
}