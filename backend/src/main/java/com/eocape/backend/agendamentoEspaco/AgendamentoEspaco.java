package com.eocape.backend.agendamentoEspaco;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.espaco_comum.EspacoComum;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa reservas de espaços comuns e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "agendamento_espaco", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendamentoEspaco {

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
     * Espaço comum ao qual a reserva ou configuração se refere.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espaco_comum_id", nullable = false, updatable = false)
    private EspacoComum espacoComum;

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
     * Inicializa AgendamentoEspaco com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param espacoComum Espaço comum ao qual a reserva ou configuração se refere.
     * @param usuario Usuário associado ao registro.
     * @param unidade Unidade habitacional associada ao registro.
     * @param inicio Data e hora de início da reserva.
     * @param fim Data e hora de término da reserva, posterior ao início.
     * @param observacao Observação complementar da operação.
     * @param status Estado atual do registro no seu fluxo de atendimento.
     */
    public AgendamentoEspaco(Condominio condominio, EspacoComum espacoComum, Usuario usuario,
            Unidade unidade, Instant inicio, Instant fim,
            String observacao, StatusAgendamento status) {
        this.condominio = condominio;
        this.espacoComum = espacoComum;
        this.usuario = usuario;
        this.unidade = unidade;
        this.inicio = inicio;
        this.fim = fim;
        this.observacao = observacao;
        this.status = status;
    }

    /**
     * Data e hora de início da reserva.
     */
    @Column(name = "inicio", nullable = false)
    private Instant inicio;

    /**
     * Data e hora de término da reserva, posterior ao início.
     */
    @Column(name = "fim", nullable = false)
    private Instant fim;

    /**
     * Estado atual do registro no seu fluxo de atendimento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusAgendamento status;

    /**
     * Observação complementar da operação.
     */
    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

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
     * Inicializa AgendamentoEspaco com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param espacoComum Espaço comum ao qual a reserva ou configuração se refere.
     * @param usuario Usuário associado ao registro.
     * @param inicio Data e hora de início da reserva.
     * @param fim Data e hora de término da reserva, posterior ao início.
     * @param observacao Observação complementar da operação.
     * @param status Estado atual do registro no seu fluxo de atendimento.
     */
    public AgendamentoEspaco(Condominio condominio, EspacoComum espacoComum, Usuario usuario,
            Instant inicio, Instant fim, String observacao, StatusAgendamento status) {
        this.condominio = condominio;
        this.espacoComum = espacoComum;
        this.usuario = usuario;
        this.inicio = inicio;
        this.fim = fim;
        this.observacao = observacao;
        this.status = status;
    }

    /**
     * Altera o status de reservas de espaços comuns.
     *
     * @param novoStatus Estado que será aplicado ao registro.
     */
    public void alterarStatus(StatusAgendamento novoStatus) {
        this.status = novoStatus;
    }
}