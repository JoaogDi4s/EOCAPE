package com.eocape.backend.verificacaoEmail;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
@RequiredArgsConstructor
public class SmtpCodigoEmailSender implements CodigoEmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void enviar(String email, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("EOCAPE - Código de verificação");
        msg.setText("Seu código de verificação é: " + codigo
                + "\n\nEle expira em 10 minutos. Se você não solicitou, ignore este e-mail.");
        mailSender.send(msg);
    }
}