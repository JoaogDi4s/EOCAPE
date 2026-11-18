package com.eocape.backend.chamado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa histórico dos chamados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "historico_chamado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoricoChamado {

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
     * Usuário associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    /**
     * Estado do chamado antes da alteração.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusChamado statusAnterior;

    /**
     * Estado do chamado após a alteração.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusChamado statusNovo;

    /**
     * Comentário que contextualiza a alteração de status.
     */
    @Column(name = "comentario", columnDefinition = "text")
    private String comentario;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa HistoricoChamado com os dados e as dependências necessários à operação.
     *
     * @param chamado Chamado ao qual os dados ou o anexo pertencem.
     * @param usuario Usuário associado ao registro.
     * @param statusAnterior Estado do chamado antes da alteração.
     * @param statusNovo Estado do chamado após a alteração.
     * @param comentario Comentário que contextualiza a alteração de status.
     */
    public HistoricoChamado(Chamado chamado, Usuario usuario,
                            StatusChamado statusAnterior, StatusChamado statusNovo, String comentario) {
        this.chamado = chamado;
        this.usuario = usuario;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.comentario = comentario;
    }
}