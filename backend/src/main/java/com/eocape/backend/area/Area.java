package com.eocape.backend.area;

import com.eocape.backend.condominio.Condominio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa áreas e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "area", schema = "eocape")
@Getter
@NoArgsConstructor

public class Area {
    // id
    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // condominio_id
    /**
     * Condomínio que delimita o escopo deste registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    // nome
    /**
     * Nome usado para identificar o registro.
     */
    @Column(nullable = false, length = 100)
    private String nome;

    // descricao
    /**
     * Descrição detalhada do registro.
     */
    @Column(nullable = false, length = 200)
    private String descricao;

    // ativo
    /**
     * Indica se o registro está ativo para uso no sistema.
     */
    @Column(nullable = false)
    private boolean ativo = true;

    // criado_em
    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa Area com os dados e as dependências necessários à operação.
     *
     * @param nome Nome usado para identificar o registro.
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param descricao Descrição detalhada do registro.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public Area(String nome, Condominio condominio, String descricao, boolean ativo) {
        this.nome = nome;
        this.condominio = condominio;
        this.descricao = descricao;
        this.ativo = ativo;
    }
}
