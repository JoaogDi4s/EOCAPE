package com.eocape.backend.verificacaoEmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class SmtpCodigoEmailSender implements CodigoEmailSender {

    private final JavaMailSender mailSender;
    private final String remetente;

    public SmtpCodigoEmailSender(JavaMailSender mailSender,
                                 @Value("${app.mail.from}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String email, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(email);
        msg.setSubject("EOCAPE - Código de verificação");
        msg.setText("Seu código de verificação é: " + codigo
                + "\n\nEle expira em 10 minutos. Se você não solicitou, ignore este e-mail.");
        mailSender.send(msg);
    }
}