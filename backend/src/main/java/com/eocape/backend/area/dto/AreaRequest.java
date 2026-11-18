package com.eocape.backend.area.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de area, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param nome Nome usado para identificar o registro.
 * @param descricao Descrição detalhada do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 */
public record AreaRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
        String nome,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 200, message = "A descrição não pode ter mais de 200 caracteres")
        String descricao,

        @NotNull(message = "O ID do condomínio é obrigatório")
        UUID condominioId,

        @NotNull(message = "O status da área é obrigatório")
        Boolean ativo
) {
    /**
     * Inicializa AreaRequest com os dados e as dependências necessários à operação.
     *
     * @param nome Nome usado para identificar o registro.
     * @param descricao Descrição detalhada do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public AreaRequest {
        nome = nome == null ? null : nome.trim();
        descricao = descricao == null ? null : descricao.trim();
    }
}