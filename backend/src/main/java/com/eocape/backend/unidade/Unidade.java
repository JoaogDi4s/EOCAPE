package com.eocape.backend.unidade;

import com.eocape.backend.bloco.Bloco;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa unidades e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "unidade", schema = "eocape")
@Getter
@NoArgsConstructor

public class Unidade {
    // id
    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // bloco_id
    /**
     * Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bloco_id", nullable = false, updatable = false)
    private Bloco bloco;

    // numero
    /**
     * Número ou identificação da unidade.
     */
    @Column(nullable = false, length = 5)
    private String numero;

    // andar
    /**
     * Andar em que a unidade está localizada.
     */
    @Column(nullable = false, length = 5)
    private Integer andar;

    // criado_em
    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa Unidade com os dados e as dependências necessários à operação.
     *
     * @param bloco Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     * @param numero Número ou identificação da unidade.
     * @param andar Andar em que a unidade está localizada.
     */
    public Unidade(Bloco bloco, String numero, Integer andar) {
        this.bloco = bloco;
        this.numero = numero;
        this.andar = andar;
    }
}
