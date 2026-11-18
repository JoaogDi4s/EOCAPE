package com.eocape.backend.verificacaoEmail.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados de entrada de enviar codigo, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param email Endereço de e-mail do usuário.
 */
public record EnviarCodigoRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 100, message = "O e-mail não pode ter mais de 100 caracteres")
        String email
) {}