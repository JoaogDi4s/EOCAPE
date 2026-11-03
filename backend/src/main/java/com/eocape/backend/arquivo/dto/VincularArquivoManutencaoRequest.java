package com.eocape.backend.arquivo.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VincularArquivoManutencaoRequest(
        @NotNull
        UUID arquivoId
) {}