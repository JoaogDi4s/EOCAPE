package com.eocape.backend.vinculoUnidade;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vinculo_unidade", schema = "eocape")
@Getter
@NoArgsConstructor

public class VinculoUnidade {
    // id
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // usuario_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    // unidade_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false, updatable = false)
    private Unidade unidade;

    // tipo_vinculo
    @Column(name = "tipo_vinculo", nullable = false, length = 200)
    private String tipoVinculo;

    // principal
    @Column(name = "principal", nullable = false)
    private boolean principal;

    // ativo
    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    // criado_em
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

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
