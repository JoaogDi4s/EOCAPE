package com.eocape.backend.ata.dto;

import com.eocape.backend.ata.Ata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa os dados de ata devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param dataReuniao Data da reunião documentada pela ata.
 * @param resumo Resumo das decisões registradas na ata.
 * @param publicada Indica se a ata pode ser consultada pelos moradores vinculados.
 * @param criadoPorNome Nome do usuário que criou o registro.
 * @param criadoEm Instante de criação do registro.
 */
public record AtaResponse(
        UUID id,
        String titulo,
        LocalDate dataReuniao,
        String resumo,
        boolean publicada,
        String criadoPorNome,
        Instant criadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param ata Ata de assembleia associada ao anexo.
     * @return dados de ata produzidos pela operação e destinados à resposta da API.
     */
    public static AtaResponse from(Ata ata) {
        return new AtaResponse(
                ata.getId(),
                ata.getTitulo(),
                ata.getDataReuniao(),
                ata.getResumo(),
                ata.isPublicada(),
                ata.getCriadoPor().getNome(),
                ata.getCriadoEm()
        );
    }
}