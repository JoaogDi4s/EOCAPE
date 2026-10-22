package com.eocape.backend.aviso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AvisoRequest(
        @NotNull(message = "bloco é obrigatório") UUID blocoId,

        @NotBlank(message = "título é obrigatório") @Size(max = 200, message = "título deve ter no máximo 200 caracteres") String titulo,

        @NotBlank(message = "conteúdo é obrigatório") String conteudo,

        Boolean importante) {
    public AvisoRequest {
        importante = importante != null && importante;
    }
}