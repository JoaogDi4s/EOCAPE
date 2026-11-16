package com.eocape.backend.aviso;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "aviso", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false, updatable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloco_id")
    private Bloco bloco;

    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(name = "conteudo", nullable = false, columnDefinition = "text")
    private String conteudo;

    @Column(name = "importante", nullable = false)
    private boolean importante = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Column(name = "publicado", nullable = false)
    private boolean publicado = false;

    public void publicar() {
        this.publicado = true;
    }

    public Aviso(Condominio condominio, Usuario autor, Bloco bloco,
            String titulo, String conteudo, boolean importante) {
        this.condominio = condominio;
        this.autor = autor;
        this.bloco = bloco;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.importante = importante;
    }
}