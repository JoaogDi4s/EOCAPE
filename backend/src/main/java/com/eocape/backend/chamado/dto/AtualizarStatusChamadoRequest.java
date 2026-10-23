package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.StatusChamado;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusChamadoRequest(
        @NotNull StatusChamado status) {
}