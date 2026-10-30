package com.eocape.backend.manutencao.dto;

import com.eocape.backend.manutencao.TipoManutencao;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarManutencaoRequest(
        @NotNull
        UUID condominioId,

        UUID unidadeId,

        UUID areaId,

        UUID chamadoId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String descricao,

        @NotNull
        TipoManutencao tipo
) {
    /** O banco exige exatamente um entre unidade e área. */
    @AssertTrue(message = "Informe a unidade ou a área, mas não as duas")
    public boolean isVinculoUnico() {
        return (unidadeId != null) ^ (areaId != null);
    }
}