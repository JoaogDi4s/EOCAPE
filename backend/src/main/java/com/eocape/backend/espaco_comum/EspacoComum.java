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

/**
 * Representa espaços comuns e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "espaco_comum", schema = "eocape")
@Getter
@NoArgsConstructor
public class EspacoComum {
    // id uuid
    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // area_id uuid
    /**
     * Área comum relacionada ao espaço ou ao atendimento.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false, updatable = false)
    private Area area;

    // capacidade int4
    /**
     * Quantidade de pessoas comportadas pelo espaço comum.
     */
    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    // horario_abertura time
    /**
     * Horário inicial permitido para utilização do espaço.
     */
    @Column(name = "horario_abertura")
    private LocalTime horarioAbertura;

    // horario_fechamento time
    /**
     * Horário final permitido para utilização do espaço.
     */
    @Column(name = "horario_fechamento")
    private LocalTime horarioFechamento;

    // taxa numeric
    /**
     * Valor cobrado pela utilização do espaço comum.
     */
    @Column(name = "taxa", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxa;

    // exige_aprovacao bool
    /**
     * Indica se a reserva depende de aprovação por um gestor.
     */
    @Column(name = "exige_aprovacao", nullable = false)
    private boolean exigeAprovacao = true;

    // regras text
    /**
     * Regras de utilização do espaço comum.
     */
    @Column(name = "regras", nullable = false, columnDefinition = "text")
    private String regras;

    // ativo bool
    /**
     * Indica se o registro está ativo para uso no sistema.
     */
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    // criado_em timestampz
    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa EspacoComum com os dados e as dependências necessários à operação.
     *
     * @param area Área comum relacionada ao espaço ou ao atendimento.
     * @param capacidade Quantidade de pessoas comportadas pelo espaço comum.
     * @param horarioAbertura Horário inicial permitido para utilização do espaço.
     * @param horarioFechamento Horário final permitido para utilização do espaço.
     * @param taxa Valor cobrado pela utilização do espaço comum.
     * @param exigeAprovacao Indica se a reserva depende de aprovação por um gestor.
     * @param regras Regras de utilização do espaço comum.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
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