package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.classificado.Classificado;

import java.util.UUID;

@Entity
@Table(name = "arquivo_classificado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoClassificado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classificado_id", nullable = false, updatable = false)
    private Classificado classificado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    public ArquivoClassificado(Classificado classificado, Arquivo arquivo) {
        this.classificado = classificado;
        this.arquivo = arquivo;
    }
}