package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "arquivo", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enviado_por_id", nullable = false, updatable = false)
    private Usuario enviadoPor;

    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    @Column(name = "chave_armazenamento", nullable = false, unique = true, length = 500)
    private String chaveArmazenamento;

    @Column(name = "tipo_mime", length = 100)
    private String tipoMime;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Arquivo(Condominio condominio, Usuario enviadoPor, String nomeOriginal,
                   String chaveArmazenamento, String tipoMime) {
        this.condominio = condominio;
        this.enviadoPor = enviadoPor;
        this.nomeOriginal = nomeOriginal;
        this.chaveArmazenamento = chaveArmazenamento;
        this.tipoMime = tipoMime;
    }
}