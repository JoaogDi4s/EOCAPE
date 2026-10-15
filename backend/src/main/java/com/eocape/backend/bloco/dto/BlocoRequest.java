package com.eocape.backend.bloco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BlocoRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
        String nome,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId

) {
    public BlocoRequest {
        nome = nome == null ? null : nome.trim();
    }
}