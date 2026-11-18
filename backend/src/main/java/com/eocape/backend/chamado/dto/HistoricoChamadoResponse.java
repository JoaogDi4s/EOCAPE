package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.HistoricoChamado;
import com.eocape.backend.chamado.StatusChamado;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de historico chamado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param chamadoId Identificador do chamado.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param usuarioNome Nome do usuário exibido na resposta.
 * @param statusAnterior Estado do chamado antes da alteração.
 * @param statusNovo Estado do chamado após a alteração.
 * @param comentario Comentário que contextualiza a alteração de status.
 * @param criadoEm Instante de criação do registro.
 */
public record HistoricoChamadoResponse(
        UUID id,
        UUID chamadoId,
        UUID usuarioId,
        String usuarioNome,
        StatusChamado statusAnterior,
        StatusChamado statusNovo,
        String comentario,
        Instant criadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param h Entidade de origem do tipo HistoricoChamado, utilizada para montar os dados da resposta.
     * @return dados de historico chamado produzidos pela operação e destinados à resposta da API.
     */
    public static HistoricoChamadoResponse from(HistoricoChamado h) {
        return new HistoricoChamadoResponse(
                h.getId(),
                h.getChamado().getId(),
                h.getUsuario().getId(),
                h.getUsuario().getNome(),
                h.getStatusAnterior(),
                h.getStatusNovo(),
                h.getComentario(),
                h.getCriadoEm());
    }
}