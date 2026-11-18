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

/**
 * Representa arquivos e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "arquivo", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Arquivo {

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
     * Usuário responsável pelo envio do arquivo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enviado_por_id", nullable = false, updatable = false)
    private Usuario enviadoPor;

    /**
     * Nome original do arquivo apresentado ao usuário.
     */
    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    /**
     * Caminho do objeto no bucket privado de arquivos.
     */
    @Column(name = "chave_armazenamento", nullable = false, unique = true, length = 500)
    private String chaveArmazenamento;

    /**
     * Tipo MIME do conteúdo armazenado.
     */
    @Column(name = "tipo_mime", length = 100)
    private String tipoMime;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa Arquivo com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param enviadoPor Usuário responsável pelo envio do arquivo.
     * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
     * @param chaveArmazenamento Caminho do objeto no bucket privado de arquivos.
     * @param tipoMime Tipo MIME do conteúdo armazenado.
     */
    public Arquivo(Condominio condominio, Usuario enviadoPor, String nomeOriginal,
                   String chaveArmazenamento, String tipoMime) {
        this.condominio = condominio;
        this.enviadoPor = enviadoPor;
        this.nomeOriginal = nomeOriginal;
        this.chaveArmazenamento = chaveArmazenamento;
        this.tipoMime = tipoMime;
    }
}