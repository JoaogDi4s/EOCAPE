package com.eocape.backend.aviso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados de entrada de aviso atualizacao, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
 * @param importante Indica se o comunicado deve receber destaque.
 */
public record AvisoAtualizacaoRequest(
        @NotBlank(message = "título é obrigatório")
        @Size(max = 200, message = "título deve ter no máximo 200 caracteres")
        String titulo,

        @NotBlank(message = "conteúdo é obrigatório")
        String conteudo,

        Boolean importante
) {
    /**
     * Inicializa AvisoAtualizacaoRequest com os dados e as dependências necessários à operação.
     *
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @param importante Indica se o comunicado deve receber destaque.
     */
    public AvisoAtualizacaoRequest {
        importante = importante != null && importante;
    }
}