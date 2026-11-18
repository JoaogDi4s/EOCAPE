package com.eocape.backend.vinculoUnidade;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa vínculos entre usuários e unidades e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "vinculo_unidade", schema = "eocape")
@Getter
@NoArgsConstructor

public class VinculoUnidade {
    // id
    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // usuario_id
    /**
     * Usuário associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    // unidade_id
    /**
     * Unidade habitacional associada ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false, updatable = false)
    private Unidade unidade;

    // tipo_vinculo
    /**
     * Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     */
    @Column(name = "tipo_vinculo", nullable = false, length = 200)
    private String tipoVinculo;

    // principal
    /**
     * Indica se este é o vínculo principal do usuário.
     */
    @Column(name = "principal", nullable = false)
    private boolean principal;

    // ativo
    /**
     * Indica se o registro está ativo para uso no sistema.
     */
    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    // criado_em
    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa VinculoUnidade com os dados e as dependências necessários à operação.
     *
     * @param usuario Usuário associado ao registro.
     * @param unidade Unidade habitacional associada ao registro.
     * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     * @param principal Indica se este é o vínculo principal do usuário.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     * @param criadoEm Instante de criação do registro.
     */
    public VinculoUnidade(Usuario usuario, Unidade unidade, String tipoVinculo, boolean principal, boolean ativo,
            Instant criadoEm) {
        this.usuario = usuario;
        this.unidade = unidade;
        this.tipoVinculo = tipoVinculo;
        this.principal = principal;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
    }
}
