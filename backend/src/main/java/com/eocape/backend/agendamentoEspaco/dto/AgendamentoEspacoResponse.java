package com.eocape.backend.agendamentoEspaco.dto;

import com.eocape.backend.agendamentoEspaco.AgendamentoEspaco;
import com.eocape.backend.agendamentoEspaco.StatusAgendamento;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de agendamento espaco devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param espacoComumId Identificador do espaço comum reservável.
 * @param areaNome Nome da área comum exibido na resposta.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param usuarioNome Nome do usuário exibido na resposta.
 * @param inicio Data e hora de início da reserva.
 * @param fim Data e hora de término da reserva, posterior ao início.
 * @param status Estado atual do registro no seu fluxo de atendimento.
 * @param observacao Observação complementar da operação.
 * @param criadoEm Instante de criação do registro.
 * @param atualizadoEm Instante da última atualização do registro.
 */
public record AgendamentoEspacoResponse(
        UUID id,
        UUID condominioId,
        UUID espacoComumId,
        String areaNome,
        UUID usuarioId,
        String usuarioNome,
        Instant inicio,
        Instant fim,
        StatusAgendamento status,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param a Entidade de origem do tipo AgendamentoEspaco, utilizada para montar os dados da resposta.
     * @return dados de agendamento espaco produzidos pela operação e destinados à resposta da API.
     */
    public static AgendamentoEspacoResponse from(AgendamentoEspaco a) {
        return new AgendamentoEspacoResponse(
                a.getId(),
                a.getCondominio().getId(),
                a.getEspacoComum().getId(),
                a.getEspacoComum().getArea().getNome(),
                a.getUsuario().getId(),
                a.getUsuario().getNome(),
                a.getInicio(),
                a.getFim(),
                a.getStatus(),
                a.getObservacao(),
                a.getCriadoEm(),
                a.getAtualizadoEm());
    }
}