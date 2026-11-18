package com.eocape.backend.auth;

import java.time.Duration;

/**
 * Define os valores admitidos para finalidade no domínio do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public enum Finalidade {
    /**
     * Permite verificar o e-mail durante os 30 minutos seguintes à emissão do token.
     */
    PRIMEIRO_ACESSO(Duration.ofMinutes(30)),
    /**
     * Permite definir a senha no primeiro acesso durante dez minutos.
     */
    DEFINIR_SENHA(Duration.ofMinutes(10)),
    /**
     * Autoriza as operações usuais do sistema durante quinze minutos.
     */
    ACESSO(Duration.ofMinutes(15));

    /**
     * Prazo de validade aplicado ao código ou token.
     */
    private final Duration validade;

    /**
     * Inicializa Finalidade com os dados e as dependências necessários à operação.
     *
     * @param validade Prazo de validade aplicado ao código ou token.
     */
    Finalidade(Duration validade) {
        this.validade = validade;
    }

    /**
     * Obtém o prazo de validade associado à finalidade do token.
     *
     * @return duração da validade configurada para a finalidade.
     */
    public Duration validade() {
        return validade;
    }
}