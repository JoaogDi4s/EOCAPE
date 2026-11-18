package com.eocape.backend.verificacaoEmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Envia códigos de verificação por meio do servidor SMTP configurado.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Component
@Profile("!dev")
public class SmtpCodigoEmailSender implements CodigoEmailSender {

    /**
     * Componente responsável pelo envio das mensagens de verificação.
     */
    private final JavaMailSender mailSender;
    /**
     * Endereço de e-mail remetente configurado para as mensagens de verificação.
     */
    private final String remetente;

    /**
     * Inicializa SmtpCodigoEmailSender com os dados e as dependências necessários à operação.
     *
     * @param mailSender Componente responsável pelo envio das mensagens de verificação.
     * @param remetente Endereço de e-mail remetente configurado para as mensagens de verificação.
     */
    public SmtpCodigoEmailSender(JavaMailSender mailSender,
                                 @Value("${app.mail.from}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    /**
     * Envia smtp codigo email sender.
     *
     * @param email Endereço de e-mail do usuário.
     * @param codigo Código de verificação informado ou enviado ao usuário.
     */
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