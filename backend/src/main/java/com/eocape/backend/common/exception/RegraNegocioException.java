package com.eocape.backend.common.exception;

/**
 * Sinaliza uma violação das regras de negócio que deve ser tratada pela camada HTTP.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public class RegraNegocioException extends RuntimeException {

    /**
     * Inicializa RegraNegocioException com os dados e as dependências necessários à operação.
     *
     * @param message Mensagem descritiva da violação de regra de negócio.
     */
    public RegraNegocioException(String message) {
        super(message);
    }
}