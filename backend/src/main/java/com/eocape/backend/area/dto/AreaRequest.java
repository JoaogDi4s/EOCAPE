package com.eocape.backend.area.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AreaRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
        String nome,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 200, message = "A descrição não pode ter mais de 200 caracteres")
        String descricao,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId,

        @NotNull(message = "O status da área é obrigatório")
        Boolean ativo
) {
    public AreaRequest {
        nome = nome == null ? null : nome.trim();
        descricao = descricao == null ? null : descricao.trim();
    }
}