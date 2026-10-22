package com.eocape.backend.area;

import com.eocape.backend.condominio.Condominio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "area", schema = "eocape")
@Getter
@NoArgsConstructor

public class Area {
    // id
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // condominio_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    // nome
    @Column(nullable = false, length = 100)
    private String nome;

    // descricao
    @Column(nullable = false, length = 200)
    private String descricao;

    // ativo
    @Column(nullable = false)
    private boolean ativo = true;

    // criado_em
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Area(String nome, Condominio condominio, String descricao, boolean ativo) {
        this.nome = nome;
        this.condominio = condominio;
        this.descricao = descricao;
        this.ativo = ativo;
    }
}
