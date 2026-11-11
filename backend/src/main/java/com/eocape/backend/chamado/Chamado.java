package com.eocape.backend.chamado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.area.Area;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chamado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false, updatable = false)
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "text")
    private String descricao;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusChamado status;

    @Column(name = "concluido_em")
    private Instant concluidoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Chamado(Condominio condominio, Usuario usuario, Unidade unidade, Area area,
            String titulo, String descricao, String categoria) {
        this.condominio = condominio;
        this.usuario = usuario;
        this.unidade = unidade;
        this.area = area;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.status = StatusChamado.ABERTO;
    }

    public void atualizarStatus(StatusChamado novoStatus) {
        this.status = novoStatus;
        this.concluidoEm = novoStatus == StatusChamado.RESOLVIDO ? Instant.now() : null;
    }
}