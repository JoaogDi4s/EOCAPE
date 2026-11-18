package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.ata.Ata;

import java.util.UUID;

/**
 * Representa anexos de atas e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "arquivo_ata", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoAta {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Ata de assembleia associada ao anexo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ata_id", nullable = false, updatable = false)
    private Ata ata;

    /**
     * Metadados do arquivo vinculado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    /**
     * Inicializa ArquivoAta com os dados e as dependências necessários à operação.
     *
     * @param ata Ata de assembleia associada ao anexo.
     * @param arquivo Metadados do arquivo vinculado ao registro.
     */
    public ArquivoAta(Ata ata, Arquivo arquivo) {
        this.ata = ata;
        this.arquivo = arquivo;
    }
}