package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.StatusClassificado;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusClassificadoRequest(
        @NotNull
        StatusClassificado status
) {}