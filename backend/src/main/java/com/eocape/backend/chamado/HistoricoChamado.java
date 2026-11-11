package com.eocape.backend.chamado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historico_chamado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoricoChamado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chamado_id", nullable = false, updatable = false)
    private Chamado chamado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusChamado statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusChamado statusNovo;

    @Column(name = "comentario", columnDefinition = "text")
    private String comentario;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public HistoricoChamado(Chamado chamado, Usuario usuario,
                            StatusChamado statusAnterior, StatusChamado statusNovo, String comentario) {
        this.chamado = chamado;
        this.usuario = usuario;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.comentario = comentario;
    }
}