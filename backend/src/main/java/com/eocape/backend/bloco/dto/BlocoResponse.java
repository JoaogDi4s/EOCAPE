package com.eocape.backend.bloco.dto;

import com.eocape.backend.bloco.Bloco;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de bloco devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param nome Nome usado para identificar o registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param criadoEm Instante de criação do registro.
 */
public record BlocoResponse(
        UUID id,
        String nome,
        UUID condominioId,
        Instant criadoEm) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param bloco Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     * @return dados de bloco produzidos pela operação e destinados à resposta da API.
     */
    public static BlocoResponse from(Bloco bloco) {
        return new BlocoResponse(
                bloco.getId(),
                bloco.getNome(),
                bloco.getCondominio().getId(),
                bloco.getCriadoEm());
    }
}