package com.eocape.backend.auth;

import java.util.UUID;

public record Autenticacao(UUID usuarioId, Finalidade finalidade) {}