package com.eocape.backend.papelCondominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de papel condominio, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param papel Papel de autorização atribuído ao usuário no condomínio.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 */
public record PapelCondominioRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId,

        @NotBlank(message = "O papel do usuário é obrigatório")
        @Size(max = 200, message = "O papel do usuário não pode ter mais de 200 caracteres")
        String papel,

        boolean ativo) {

    /**
     * Inicializa PapelCondominioRequest com os dados e as dependências necessários à operação.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param papel Papel de autorização atribuído ao usuário no condomínio.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public PapelCondominioRequest {
        papel = papel == null ? null : papel.trim();
    }
}