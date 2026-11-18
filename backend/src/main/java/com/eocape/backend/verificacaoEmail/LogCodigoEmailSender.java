package com.eocape.backend.verificacaoEmail;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Provisório: só registra o código no log. Substituir pelo envio real de e-mail.
/**
 * Disponibiliza o envio de códigos em log para o ambiente de desenvolvimento.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Profile("dev")
@Slf4j
@Component
public class LogCodigoEmailSender implements CodigoEmailSender {

    /**
     * Envia log codigo email sender.
     *
     * @param email Endereço de e-mail do usuário.
     * @param codigo Código de verificação informado ou enviado ao usuário.
     */
    @Override
    public void enviar(String email, String codigo) {
        log.info("[DEV] Código de verificação para {}: {}", email, codigo);
    }
}