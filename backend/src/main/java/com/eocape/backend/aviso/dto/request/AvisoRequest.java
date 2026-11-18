package com.eocape.backend.aviso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de aviso, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param blocoId Identificador do bloco do condomínio.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
 * @param importante Indica se o comunicado deve receber destaque.
 */
public record AvisoRequest(
        @NotNull(message = "bloco é obrigatório") UUID blocoId,

        @NotBlank(message = "título é obrigatório") @Size(max = 200, message = "título deve ter no máximo 200 caracteres") String titulo,

        @NotBlank(message = "conteúdo é obrigatório") String conteudo,

        Boolean importante) {
    /**
     * Inicializa AvisoRequest com os dados e as dependências necessários à operação.
     *
     * @param blocoId Identificador do bloco do condomínio.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @param importante Indica se o comunicado deve receber destaque.
     */
    public AvisoRequest {
        importante = importante != null && importante;
    }
}