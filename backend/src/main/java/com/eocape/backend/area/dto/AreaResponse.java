package com.eocape.backend.area.dto;

import com.eocape.backend.area.Area;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de area devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param nome Nome usado para identificar o registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param descricao Descrição detalhada do registro.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 * @param criadoEm Instante de criação do registro.
 */
public record AreaResponse(
        UUID id,
        String nome,
        UUID condominioId,
        String descricao,
        boolean ativo,
        Instant criadoEm) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param area Área comum relacionada ao espaço ou ao atendimento.
     * @return dados de area produzidos pela operação e destinados à resposta da API.
     */
    public static AreaResponse from(Area area) {
        return new AreaResponse(
                area.getId(),
                area.getNome(),
                area.getCondominio().getId(),
                area.getDescricao(),
                area.isAtivo(),
                area.getCriadoEm());
    }
}