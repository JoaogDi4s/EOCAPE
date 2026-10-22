package com.eocape.backend.papelCondominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "papel_condominio", schema = "eocape")
@Getter
@NoArgsConstructor
public class PapelCondominio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @Column(name = "papel", nullable = false, length = 200)
    private String papel;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public PapelCondominio(Usuario usuario, Condominio condominio, String papel, boolean ativo) {
        this.usuario = usuario;
        this.condominio = condominio;
        this.papel = papel;
        this.ativo = ativo;
    }

    public void desativar() {
        this.ativo = false;
    }
}