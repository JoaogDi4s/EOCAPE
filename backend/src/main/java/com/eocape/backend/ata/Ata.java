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

/**
 * Representa atas de assembleia e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "ata", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ata {

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
     * Usuário responsável pela criação do registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criado_por_id", nullable = false, updatable = false)
    private Usuario criadoPor;

    /**
     * Título apresentado na listagem e nos detalhes do registro.
     */
    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    /**
     * Data da reunião documentada pela ata.
     */
    @Column(name = "data_reuniao", nullable = false)
    private LocalDate dataReuniao;

    /**
     * Resumo das decisões registradas na ata.
     */
    @Column(name = "resumo", nullable = false, columnDefinition = "text")
    private String resumo;

    /**
     * Indica se a ata pode ser consultada pelos moradores vinculados.
     */
    @Column(name = "publicada", nullable = false)
    private boolean publicada = false;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa Ata com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param criadoPor Usuário responsável pela criação do registro.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param dataReuniao Data da reunião documentada pela ata.
     * @param resumo Resumo das decisões registradas na ata.
     */
    public Ata(Condominio condominio, Usuario criadoPor, String titulo,
            LocalDate dataReuniao, String resumo) {
        this.condominio = condominio;
        this.criadoPor = criadoPor;
        this.titulo = titulo;
        this.dataReuniao = dataReuniao;
        this.resumo = resumo;
    }

    /**
     * Publica a ata ou o comunicado de acordo com o recurso desta classe.
     */
    public void publicar() {
        this.publicada = true;
    }
}