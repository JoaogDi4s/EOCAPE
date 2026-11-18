package com.eocape.backend.manutencao;

/**
 * Define os valores admitidos para status manutencao no domínio do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public enum StatusManutencao {
    /**
     * Estado ou opção agendada admitido por status manutencao.
     */
    AGENDADA,
    /**
     * Estado ou opção em andamento admitido por status manutencao.
     */
    EM_ANDAMENTO,
    /**
     * Estado ou opção concluida admitido por status manutencao.
     */
    CONCLUIDA,
    /**
     * Estado ou opção cancelada admitido por status manutencao.
     */
    CANCELADA
}