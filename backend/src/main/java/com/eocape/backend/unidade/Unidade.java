package com.eocape.backend.unidade;

import com.eocape.backend.bloco.Bloco;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "unidade", schema = "eocape")
@Getter
@NoArgsConstructor

public class Unidade {
    // id
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // bloco_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bloco_id", nullable = false, updatable = false)
    private Bloco bloco;

    // numero
    @Column(nullable = false, length = 5)
    private String numero;

    // andar
    @Column(nullable = false, length = 5)
    private Integer andar;

    // criado_em
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Unidade(Bloco bloco, String numero, Integer andar) {
        this.bloco = bloco;
        this.numero = numero;
        this.andar = andar;
    }
}
