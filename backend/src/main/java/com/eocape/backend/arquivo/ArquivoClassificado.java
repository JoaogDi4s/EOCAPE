package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.classificado.Classificado;

import java.util.UUID;

/**
 * Representa anexos de classificados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "arquivo_classificado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoClassificado {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Anúncio classificado associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classificado_id", nullable = false, updatable = false)
    private Classificado classificado;

    /**
     * Metadados do arquivo vinculado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    /**
     * Inicializa ArquivoClassificado com os dados e as dependências necessários à operação.
     *
     * @param classificado Anúncio classificado associado ao registro.
     * @param arquivo Metadados do arquivo vinculado ao registro.
     */
    public ArquivoClassificado(Classificado classificado, Arquivo arquivo) {
        this.classificado = classificado;
        this.arquivo = arquivo;
    }
}