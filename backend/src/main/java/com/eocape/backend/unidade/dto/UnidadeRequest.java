package com.eocape.backend.unidade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UnidadeRequest(

        @NotBlank(message = "O número é obrigatório") @Size(max = 5, message = "O número não pode ter mais de 5 caracteres") String numero,

        @NotNull(message = "O andar é obrigatório") @Min(value = 0, message = "O andar não pode ser negativo") Integer andar,

        @NotNull(message = "O ID do bloco é obrigatório") UUID blocoId

) {
    public UnidadeRequest {
        numero = numero == null ? null : numero.trim();
    }
}