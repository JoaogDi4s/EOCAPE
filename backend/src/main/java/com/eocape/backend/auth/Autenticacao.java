package com.eocape.backend.auth;

import java.util.UUID;

/**
 * Representa os dados de autenticacao devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param finalidade Finalidade do token, que determina as permissões e sua validade.
 */
public record Autenticacao(UUID usuarioId, Finalidade finalidade) {}