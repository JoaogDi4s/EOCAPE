package com.eocape.backend.usuario.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}