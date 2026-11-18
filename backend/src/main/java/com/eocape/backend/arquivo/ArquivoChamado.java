package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.chamado.Chamado;

import java.util.UUID;

/**
 * Representa anexos de chamados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "arquivo_chamado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoChamado {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Chamado ao qual os dados ou o anexo pertencem.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chamado_id", nullable = false, updatable = false)
    private Chamado chamado;

    /**
     * Metadados do arquivo vinculado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    /**
     * Inicializa ArquivoChamado com os dados e as dependências necessários à operação.
     *
     * @param chamado Chamado ao qual os dados ou o anexo pertencem.
     * @param arquivo Metadados do arquivo vinculado ao registro.
     */
    public ArquivoChamado(Chamado chamado, Arquivo arquivo) {
        this.chamado = chamado;
        this.arquivo = arquivo;
    }
}