package com.eocape.backend.verificacaoEmail;

public interface CodigoEmailSender {

    void enviar(String email, String codigo);
}