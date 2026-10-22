package com.eocape.backend.papelCondominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PapelCondominioRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId,

        @NotBlank(message = "O papel do usuário é obrigatório")
        @Size(max = 200, message = "O papel do usuário não pode ter mais de 200 caracteres")
        String papel,

        boolean ativo) {

    public PapelCondominioRequest {
        papel = papel == null ? null : papel.trim();
    }
}