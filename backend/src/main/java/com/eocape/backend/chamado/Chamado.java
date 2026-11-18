package com.eocape.backend.chamado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.area.Area;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa chamados de manutenção e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "chamado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chamado {

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
     * Usuário associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    /**
     * Unidade habitacional associada ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false, updatable = false)
    private Unidade unidade;

    /**
     * Área comum relacionada ao espaço ou ao atendimento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    /**
     * Título apresentado na listagem e nos detalhes do registro.
     */
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    /**
     * Descrição detalhada do registro.
     */
    @Column(name = "descricao", nullable = false, columnDefinition = "text")
    private String descricao;

    /**
     * Categoria usada para organizar o chamado.
     */
    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    /**
     * Estado atual do registro no seu fluxo de atendimento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusChamado status;

    /**
     * Instante em que o atendimento foi concluído, quando aplicável.
     */
    @Column(name = "concluido_em")
    private Instant concluidoEm;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Instante da última atualização do registro.
     */
    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    /**
     * Inicializa Chamado com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param usuario Usuário associado ao registro.
     * @param unidade Unidade habitacional associada ao registro.
     * @param area Área comum relacionada ao espaço ou ao atendimento.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param descricao Descrição detalhada do registro.
     * @param categoria Categoria usada para organizar o chamado.
     */
    public Chamado(Condominio condominio, Usuario usuario, Unidade unidade, Area area,
            String titulo, String descricao, String categoria) {
        this.condominio = condominio;
        this.usuario = usuario;
        this.unidade = unidade;
        this.area = area;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.status = StatusChamado.ABERTO;
    }

    /**
     * Atualiza o status e o histórico de chamados de manutenção.
     *
     * @param novoStatus Estado que será aplicado ao registro.
     */
    public void atualizarStatus(StatusChamado novoStatus) {
        this.status = novoStatus;
        this.concluidoEm = novoStatus == StatusChamado.RESOLVIDO ? Instant.now() : null;
    }
}