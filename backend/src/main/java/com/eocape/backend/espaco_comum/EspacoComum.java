package com.eocape.backend.espaco_comum;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.area.Area;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "espaco_comum", schema = "eocape")
@Getter
@NoArgsConstructor
public class EspacoComum {
    // id uuid
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // area_id uuid
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false, updatable = false)
    private Area area;

    // capacidade int4
    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    // horario_abertura time
    @Column(name = "horario_abertura")
    private LocalTime horarioAbertura;

    // horario_fechamento time
    @Column(name = "horario_fechamento")
    private LocalTime horarioFechamento;

    // taxa numeric
    @Column(name = "taxa", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxa;

    // exige_aprovacao bool
    @Column(name = "exige_aprovacao", nullable = false)
    private boolean exigeAprovacao = true;

    // regras text
    @Column(name = "regras", nullable = false, columnDefinition = "text")
    private String regras;

    // ativo bool
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    // criado_em timestampz
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public EspacoComum(Area area, Integer capacidade, LocalTime horarioAbertura, LocalTime horarioFechamento,
            BigDecimal taxa, boolean exigeAprovacao, String regras,
            boolean ativo) {
        this.area = area;
        this.capacidade = capacidade;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.taxa = taxa;
        this.exigeAprovacao = exigeAprovacao;
        this.regras = regras;
        this.ativo = ativo;
    }
}