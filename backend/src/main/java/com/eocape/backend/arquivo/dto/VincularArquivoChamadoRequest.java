package com.eocape.backend.arquivo.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VincularArquivoChamadoRequest(
        @NotNull
        UUID arquivoId
) {}