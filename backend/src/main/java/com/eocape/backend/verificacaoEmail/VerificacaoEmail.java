package com.eocape.backend.verificacaoEmail;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.usuario.Usuario;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verificacao_email", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificacaoEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "codigo_hash", nullable = false)
    private String codigoHash;

    @Column(name = "expira_em", nullable = false, updatable = false)
    private Instant expiraEm;

    @Column(name = "usado_em")
    private Instant usadoEm;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public VerificacaoEmail(Usuario usuario, String email, String codigoHash, Duration validade) {
        this.usuario = usuario;
        this.email = email;
        this.codigoHash = codigoHash;
        this.expiraEm = Instant.now().plus(validade);
    }

    public void registrarTentativa() {
        this.tentativas++;
    }

    public void marcarComoUsado() {
        this.usadoEm = Instant.now();
    }

    public boolean estaExpirada() {
        return Instant.now().isAfter(expiraEm);
    }

    public boolean foiUsada() {
        return usadoEm != null;
    }
}