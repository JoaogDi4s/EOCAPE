package com.eocape.backend.manutencao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.area.Area;
import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.util.UUID;

@Entity
@Table(name = "manutencao", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id")
    private Chamado chamado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criado_por_id", nullable = false, updatable = false)
    private Usuario criadoPor;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoManutencao tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusManutencao status;

    public Manutencao(Condominio condominio, Usuario criadoPor, Unidade unidade, Area area,
                      Chamado chamado, String titulo, String descricao, TipoManutencao tipo) {
        this.condominio = condominio;
        this.criadoPor = criadoPor;
        this.unidade = unidade;
        this.area = area;
        this.chamado = chamado;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.status = StatusManutencao.AGENDADA;
    }
}