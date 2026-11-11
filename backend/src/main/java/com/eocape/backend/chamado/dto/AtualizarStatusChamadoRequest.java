package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.StatusChamado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarStatusChamadoRequest(
                @NotNull StatusChamado status,

                @Size(max = 500) String comentario) {
}