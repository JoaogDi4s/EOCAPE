package com.eocape.backend.usuario.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
        @NotBlank(message = "O CPF é obrigatório") String cpf,

        @NotBlank(message = "O nome é obrigatório") @Size(max = 100) String nome,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 100) String email,

        @NotBlank(message = "O telefone é obrigatório") @Size(max = 15) String telefone,

        @NotNull(message = "O bloco é obrigatório") @Min(value = 1, message = "Bloco inválido") Integer bloco,

        @NotNull(message = "O apartamento é obrigatório") @Min(value = 1, message = "Apartamento inválido") Integer apartamento) {
}