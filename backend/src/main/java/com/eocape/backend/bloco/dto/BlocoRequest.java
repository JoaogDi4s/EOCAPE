package com.eocape.backend.bloco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de bloco, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param nome Nome usado para identificar o registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 */
public record BlocoRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
        String nome,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId

) {
    /**
     * Inicializa BlocoRequest com os dados e as dependências necessários à operação.
     *
     * @param nome Nome usado para identificar o registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     */
    public BlocoRequest {
        nome = nome == null ? null : nome.trim();
    }
}