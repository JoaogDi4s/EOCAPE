package com.eocape.backend.usuario.dto.response;

/**
 * Representa os dados de login devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param token Token JWT recebido ou emitido pelo fluxo de autenticação.
 * @param trocaSenhaObrigatoria Indica que o primeiro acesso exige verificação de e-mail e definição de senha.
 */
public record LoginResponse(
    String token,
    boolean trocaSenhaObrigatoria
) {}