package com.eocape.backend.chamado;

/**
 * Define os valores admitidos para status chamado no domínio do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public enum StatusChamado {
    /**
     * Estado ou opção aberto admitido por status chamado.
     */
    ABERTO,
    /**
     * Estado ou opção em andamento admitido por status chamado.
     */
    EM_ANDAMENTO,
    /**
     * Estado ou opção resolvido admitido por status chamado.
     */
    RESOLVIDO,
    /**
     * Estado ou opção cancelado admitido por status chamado.
     */
    CANCELADO
}