package com.eocape.backend.unidade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de unidade, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param numero Número ou identificação da unidade.
 * @param andar Andar em que a unidade está localizada.
 * @param blocoId Identificador do bloco do condomínio.
 */
public record UnidadeRequest(

        @NotBlank(message = "O número é obrigatório") @Size(max = 5, message = "O número não pode ter mais de 5 caracteres") String numero,

        @NotNull(message = "O andar é obrigatório") @Min(value = 0, message = "O andar não pode ser negativo") Integer andar,

        @NotNull(message = "O ID do bloco é obrigatório") UUID blocoId

) {
    /**
     * Inicializa UnidadeRequest com os dados e as dependências necessários à operação.
     *
     * @param numero Número ou identificação da unidade.
     * @param andar Andar em que a unidade está localizada.
     * @param blocoId Identificador do bloco do condomínio.
     */
    public UnidadeRequest {
        numero = numero == null ? null : numero.trim();
    }
}