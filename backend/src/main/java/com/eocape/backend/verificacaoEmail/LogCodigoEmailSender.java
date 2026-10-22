package com.eocape.backend.verificacaoEmail;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// Provisório: só registra o código no log. Substituir pelo envio real de e-mail.
@Profile("dev")
@Slf4j
@Component
public class LogCodigoEmailSender implements CodigoEmailSender {

    @Override
    public void enviar(String email, String codigo) {
        log.info("[DEV] Código de verificação para {}: {}", email, codigo);
    }
}