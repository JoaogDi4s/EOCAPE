package com.eocape.backend.ata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarAtaRequest(
        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotNull
        LocalDate dataReuniao,

        @NotBlank
        String resumo,

        boolean publicada
) {}