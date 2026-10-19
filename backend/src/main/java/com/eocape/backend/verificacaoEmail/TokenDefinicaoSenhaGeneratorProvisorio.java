package com.eocape.backend.verificacaoEmail;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

// Provisório: token sem assinatura, só para testar o fluxo.
// Substituir por JWT assinado antes de qualquer uso real.
@Component
public class TokenDefinicaoSenhaGeneratorProvisorio implements TokenDefinicaoSenhaGenerator {

    @Override
    public String gerar(UUID usuarioId) {
        return Base64.getUrlEncoder().encodeToString(usuarioId.toString().getBytes());
    }
}