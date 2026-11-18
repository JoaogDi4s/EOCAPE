package com.eocape.backend.unidade.dto;

import com.eocape.backend.unidade.Unidade;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de unidade devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param numero Número ou identificação da unidade.
 * @param andar Andar em que a unidade está localizada.
 * @param blocoId Identificador do bloco do condomínio.
 * @param criadoEm Instante de criação do registro.
 */
public record UnidadeResponse(
        UUID id,
        String numero,
        Integer andar,
        UUID blocoId,
        Instant criadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param unidade Unidade habitacional associada ao registro.
     * @return dados de unidade produzidos pela operação e destinados à resposta da API.
     */
    public static UnidadeResponse from(Unidade unidade) {
        return new UnidadeResponse(
                unidade.getId(),
                unidade.getNumero(),
                unidade.getAndar(),
                unidade.getBloco().getId(),
                unidade.getCriadoEm()
        );
    }
}