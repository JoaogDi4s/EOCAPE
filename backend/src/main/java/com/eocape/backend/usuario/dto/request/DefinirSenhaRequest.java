package com.eocape.backend.usuario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados de entrada de definir senha, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param novaSenha Nova senha informada pelo usuário antes de ser codificada.
 * @param confirmacaoSenha Repetição da nova senha para verificar sua confirmação.
 */
public record DefinirSenhaRequest(
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String novaSenha,

        @NotBlank(message = "A confirmação é obrigatória")
        String confirmacaoSenha
) {}