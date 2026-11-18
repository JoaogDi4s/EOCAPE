package com.eocape.backend.agendamentoEspaco;

/**
 * Define os valores admitidos para status agendamento no domínio do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public enum StatusAgendamento {
    /**
     * Estado ou opção pendente admitido por status agendamento.
     */
    PENDENTE,
    /**
     * Estado ou opção confirmado admitido por status agendamento.
     */
    CONFIRMADO,
    /**
     * Estado ou opção cancelado admitido por status agendamento.
     */
    CANCELADO,
    /**
     * Estado ou opção concluido admitido por status agendamento.
     */
    CONCLUIDO
}