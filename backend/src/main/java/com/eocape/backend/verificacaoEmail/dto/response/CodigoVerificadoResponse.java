package com.eocape.backend.verificacaoEmail.dto.response;

/**
 * Representa os dados de codigo verificado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param tokenDefinicaoSenha Token limitado à definição da senha no primeiro acesso.
 */
public record CodigoVerificadoResponse(
        String tokenDefinicaoSenha
) {}