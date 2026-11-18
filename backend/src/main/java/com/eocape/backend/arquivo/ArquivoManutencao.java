package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.manutencao.Manutencao;

import java.util.UUID;

/**
 * Representa anexos de manutenções e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "arquivo_manutencao", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoManutencao {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Manutenção associada ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manutencao_id", nullable = false, updatable = false)
    private Manutencao manutencao;

    /**
     * Metadados do arquivo vinculado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    /**
     * Inicializa ArquivoManutencao com os dados e as dependências necessários à operação.
     *
     * @param manutencao Manutenção associada ao registro.
     * @param arquivo Metadados do arquivo vinculado ao registro.
     */
    public ArquivoManutencao(Manutencao manutencao, Arquivo arquivo) {
        this.manutencao = manutencao;
        this.arquivo = arquivo;
    }
}