package com.eocape.backend.chamado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarChamadoRequest(
        @NotNull
        UUID unidadeId,

        UUID areaId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String descricao,

        @NotBlank
        @Size(max = 100)
        String categoria
) {}