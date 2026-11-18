package com.eocape.backend.aviso;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa comunicados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "aviso", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Aviso {

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
     * Usuário que escreveu o comunicado.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false, updatable = false)
    private Usuario autor;

    /**
     * Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloco_id")
    private Bloco bloco;

    /**
     * Título apresentado na listagem e nos detalhes do registro.
     */
    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    /**
     * Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     */
    @Column(name = "conteudo", nullable = false, columnDefinition = "text")
    private String conteudo;

    /**
     * Indica se o comunicado deve receber destaque.
     */
    @Column(name = "importante", nullable = false)
    private boolean importante = false;

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
     * Indica o estado de publicação do comunicado.
     */
    @Column(name = "publicado", nullable = false)
    private boolean publicado = false;

    /**
     * Publica a ata ou o comunicado de acordo com o recurso desta classe.
     */
    public void publicar() {
        this.publicado = true;
    }
    /**
     * Atualiza comunicados.
     *
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @param importante Indica se o comunicado deve receber destaque.
     */
    public void atualizar(String titulo, String conteudo, boolean importante) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.importante = importante;
    }

    /**
     * Inicializa Aviso com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param autor Usuário que escreveu o comunicado.
     * @param bloco Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
     * @param importante Indica se o comunicado deve receber destaque.
     */
    public Aviso(Condominio condominio, Usuario autor, Bloco bloco,
            String titulo, String conteudo, boolean importante) {
        this.condominio = condominio;
        this.autor = autor;
        this.bloco = bloco;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.importante = importante;
    }
}
