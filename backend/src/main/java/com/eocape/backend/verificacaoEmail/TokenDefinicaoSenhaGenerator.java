package com.eocape.backend.verificacaoEmail;

import java.util.UUID;

public interface TokenDefinicaoSenhaGenerator {

    String gerar(UUID usuarioId);
}