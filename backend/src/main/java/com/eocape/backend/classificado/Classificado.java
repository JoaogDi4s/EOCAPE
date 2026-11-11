package com.eocape.backend.classificado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "classificado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Classificado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoClassificado tipo;

    @Column(name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusClassificado status;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Classificado(Condominio condominio, Usuario usuario, String titulo, String descricao,
                        TipoClassificado tipo, BigDecimal preco) {
        this.condominio = condominio;
        this.usuario = usuario;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.preco = preco;
        this.status = StatusClassificado.ATIVO;
    }

    public void alterarTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void alterarDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void alterarTipoEPreco(TipoClassificado tipo, BigDecimal preco) {
        this.tipo = tipo;
        this.preco = preco;
    }

    public void alterarStatus(StatusClassificado novoStatus) {
        this.status = novoStatus;
    }
}