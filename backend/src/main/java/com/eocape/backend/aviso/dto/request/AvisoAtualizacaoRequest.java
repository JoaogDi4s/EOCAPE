package com.eocape.backend.aviso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AvisoAtualizacaoRequest(
        @NotBlank(message = "título é obrigatório")
        @Size(max = 200, message = "título deve ter no máximo 200 caracteres")
        String titulo,

        @NotBlank(message = "conteúdo é obrigatório")
        String conteudo,

        Boolean importante
) {
    public AvisoAtualizacaoRequest {
        importante = importante != null && importante;
    }
}