package com.eocape.backend.agendamentoEspaco.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CriarAgendamentoEspacoRequest(
        @NotNull
        UUID espacoComumId,

        @NotNull
        OffsetDateTime inicio,

        @NotNull
        OffsetDateTime fim,

        @Size(max = 500)
        String observacao
) {
    @AssertTrue(message = "O fim deve ser posterior ao início")
    public boolean isPeriodoValido() {
        return inicio == null || fim == null || fim.isAfter(inicio);
    }
}