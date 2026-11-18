package com.eocape.backend.verificacaoEmail;

/**
 * Define o contrato para envio do código de verificação ao endereço de e-mail informado.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface CodigoEmailSender {

    /**
     * Envia codigo email sender.
     *
     * @param email Endereço de e-mail do usuário.
     * @param codigo Código de verificação informado ou enviado ao usuário.
     */
    void enviar(String email, String codigo);
}