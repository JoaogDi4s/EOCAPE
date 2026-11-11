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

@Entity
@Table(name = "agendamento_espaco", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgendamentoEspaco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espaco_comum_id", nullable = false, updatable = false)
    private EspacoComum espacoComum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    
    @JoinColumn(name = "unidade_id", nullable = false, updatable = false)
    private Unidade unidade;

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

    @Column(name = "inicio", nullable = false)
    private Instant inicio;

    @Column(name = "fim", nullable = false)
    private Instant fim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusAgendamento status;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

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

    public void alterarStatus(StatusAgendamento novoStatus) {
        this.status = novoStatus;
    }
}