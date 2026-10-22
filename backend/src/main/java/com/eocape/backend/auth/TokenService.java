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

@Service
public class TokenService {

    private final SecretKey chave;

    public TokenService(@Value("${app.jwt.secret}") String secret) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

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