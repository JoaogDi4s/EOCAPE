package com.eocape.backend.papelCondominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa papéis de acesso ao condomínio e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "papel_condominio", schema = "eocape")
@Getter
@NoArgsConstructor
public class PapelCondominio {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Usuário associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    /**
     * Condomínio que delimita o escopo deste registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominio_id", nullable = false, updatable = false)
    private Condominio condominio;

    /**
     * Papel de autorização atribuído ao usuário no condomínio.
     */
    @Column(name = "papel", nullable = false, length = 200)
    private String papel;

    /**
     * Indica se o registro está ativo para uso no sistema.
     */
    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa PapelCondominio com os dados e as dependências necessários à operação.
     *
     * @param usuario Usuário associado ao registro.
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param papel Papel de autorização atribuído ao usuário no condomínio.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public PapelCondominio(Usuario usuario, Condominio condominio, String papel, boolean ativo) {
        this.usuario = usuario;
        this.condominio = condominio;
        this.papel = papel;
        this.ativo = ativo;
    }

    /**
     * Desativa papéis de acesso ao condomínio.
     */
    public void desativar() {
        this.ativo = false;
    }
}