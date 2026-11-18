package com.eocape.backend.bloco;

import com.eocape.backend.condominio.Condominio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa blocos e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "bloco", schema = "eocape")
@Getter
@NoArgsConstructor
public class Bloco {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Condomínio que delimita o escopo deste registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    /**
     * Nome usado para identificar o registro.
     */
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa Bloco com os dados e as dependências necessários à operação.
     *
     * @param nome Nome usado para identificar o registro.
     * @param condominio Condomínio que delimita o escopo deste registro.
     */
    public Bloco(String nome, Condominio condominio) {
        this.nome = nome;
        this.condominio = condominio;
    }
}