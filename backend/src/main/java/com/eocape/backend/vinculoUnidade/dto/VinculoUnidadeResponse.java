package com.eocape.backend.vinculoUnidade.dto;

import com.eocape.backend.vinculoUnidade.VinculoUnidade;

import java.util.UUID;

/**
 * Representa os dados de vinculo unidade devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param unidadeId Identificador da unidade habitacional.
 * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
 * @param principal Indica se este é o vínculo principal do usuário.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 */
public record VinculoUnidadeResponse(
        UUID id,
        UUID usuarioId,
        UUID unidadeId,
        String tipoVinculo,
        boolean principal,
        boolean ativo) {

    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param vinculo Vínculo cadastral do usuário com a unidade habitacional.
     * @return dados de vinculo unidade produzidos pela operação e destinados à resposta da API.
     */
    public static VinculoUnidadeResponse from(VinculoUnidade vinculo) {
        return new VinculoUnidadeResponse(
                vinculo.getId(),
                vinculo.getUsuario().getId(),
                vinculo.getUnidade().getId(),
                vinculo.getTipoVinculo(),
                vinculo.isPrincipal(),
                vinculo.isAtivo());
    }
}