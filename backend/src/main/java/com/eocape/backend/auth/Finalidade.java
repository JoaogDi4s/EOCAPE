package com.eocape.backend.auth;

import java.time.Duration;

public enum Finalidade {
    PRIMEIRO_ACESSO(Duration.ofMinutes(30)),
    DEFINIR_SENHA(Duration.ofMinutes(10)),
    ACESSO(Duration.ofMinutes(15));

    private final Duration validade;

    Finalidade(Duration validade) {
        this.validade = validade;
    }

    public Duration validade() {
        return validade;
    }
}