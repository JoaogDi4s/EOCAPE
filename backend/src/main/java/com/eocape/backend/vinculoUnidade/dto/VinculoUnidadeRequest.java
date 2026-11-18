package com.eocape.backend.vinculoUnidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Transporta os dados de entrada de vinculo unidade, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param unidadeId Identificador da unidade habitacional.
 * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
 * @param principal Indica se este é o vínculo principal do usuário.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 */
public record VinculoUnidadeRequest(
        @NotNull(message = "O ID do usuário é obrigatório") UUID usuarioId,

        @NotNull(message = "O ID da unidade é obrigatório") UUID unidadeId,

        @NotBlank(message = "O tipo de vínculo é obrigatório") @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE", message = "Escolha proprietário, inquilino ou dependente") @Size(max = 100, message = "O tipo de vínculo não pode ter mais de 100 caracteres") String tipoVinculo,

        boolean principal,

        boolean ativo) {

    /**
     * Inicializa VinculoUnidadeRequest com os dados e as dependências necessários à operação.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param unidadeId Identificador da unidade habitacional.
     * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     * @param principal Indica se este é o vínculo principal do usuário.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public VinculoUnidadeRequest {
        tipoVinculo = tipoVinculo == null ? null : tipoVinculo.trim();
    }
}
