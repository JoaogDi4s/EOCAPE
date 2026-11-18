package com.eocape.backend.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emite e valida tokens JWT com finalidade e prazo de validade definidos.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class TokenService {

    /**
     * Chave utilizada na assinatura de tokens ou na identificação do objeto armazenado, conforme o tipo.
     */
    private final SecretKey chave;

    /**
     * Inicializa TokenService com os dados e as dependências necessários à operação.
     *
     * @param secret Segredo usado para derivar a chave de assinatura dos tokens.
     */
    public TokenService(@Value("${app.jwt.secret}") String secret) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Assina um token JWT para o usuário, incluindo sua finalidade e expiração.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param finalidade Finalidade do token, que determina as permissões e sua validade.
     * @return token JWT assinado com usuário, finalidade e expiração.
     */
    public String gerar(UUID usuarioId, Finalidade finalidade) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("fin", finalidade.name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(finalidade.validade())))
                .signWith(chave)
                .compact();
    }

    /**
     * Verifica a assinatura, a expiração e os dados do token sem propagar tokens inválidos.
     *
     * @param token Token JWT recebido ou emitido pelo fluxo de autenticação.
     * @return autenticação extraída do token, ou um Optional vazio se ele for inválido ou estiver expirado.
     */
    public Optional<Autenticacao> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID usuarioId = UUID.fromString(claims.getSubject());
            Finalidade finalidade = Finalidade.valueOf(claims.get("fin", String.class));
            return Optional.of(new Autenticacao(usuarioId, finalidade));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}   