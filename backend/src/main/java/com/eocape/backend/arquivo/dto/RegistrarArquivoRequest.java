package com.eocape.backend.arquivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegistrarArquivoRequest(
        @NotNull
        UUID condominioId,

        @NotBlank
        @Size(max = 255)
        String nomeOriginal,

        @NotBlank
        @Size(max = 500)
        String chaveArmazenamento,

        @NotBlank
        @Size(max = 100)
        String tipoMime
) {}