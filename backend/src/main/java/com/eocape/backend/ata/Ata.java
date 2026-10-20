package com.eocape.backend.ata;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ata", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criado_por_id", nullable = false, updatable = false)
    private Usuario criadoPor;

    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(name = "data_reuniao", nullable = false)
    private LocalDate dataReuniao;

    @Column(name = "resumo", nullable = false, columnDefinition = "text")
    private String resumo;

    @Column(name = "publicada", nullable = false)
    private boolean publicada = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Ata(Condominio condominio, Usuario criadoPor, String titulo,
            LocalDate dataReuniao, String resumo) {
        this.condominio = condominio;
        this.criadoPor = criadoPor;
        this.titulo = titulo;
        this.dataReuniao = dataReuniao;
        this.resumo = resumo;
    }

    public void publicar() {
        this.publicada = true;
    }
}