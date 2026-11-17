package com.eocape.backend.vinculoUnidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VinculoUnidadeRequest(
        @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

        @NotNull(message = "O ID da unidade é obrigatório") UUID unidadeId,

        @NotBlank(message = "O tipo de vínculo é obrigatório") @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE", message = "Escolha proprietário, inquilino ou dependente") @Size(max = 100, message = "O tipo de vínculo não pode ter mais de 100 caracteres") String tipoVinculo,

        boolean principal,

        boolean ativo) {

    public VinculoUnidadeRequest {
        tipoVinculo = tipoVinculo == null ? null : tipoVinculo.trim();
    }
}
