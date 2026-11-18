package com.eocape.backend.manutencao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.area.Area;
import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;

import java.util.UUID;

/**
 * Representa manutenções e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "manutencao", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manutencao {

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
     * Unidade habitacional associada ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    /**
     * Área comum relacionada ao espaço ou ao atendimento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    /**
     * Chamado ao qual os dados ou o anexo pertencem.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chamado_id")
    private Chamado chamado;

    /**
     * Usuário responsável pela criação do registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criado_por_id", nullable = false, updatable = false)
    private Usuario criadoPor;

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
     * Tipo do registro, conforme os valores admitidos pelo domínio.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoManutencao tipo;

    /**
     * Estado atual do registro no seu fluxo de atendimento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusManutencao status;

    /**
     * Inicializa Manutencao com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param criadoPor Usuário responsável pela criação do registro.
     * @param unidade Unidade habitacional associada ao registro.
     * @param area Área comum relacionada ao espaço ou ao atendimento.
     * @param chamado Chamado ao qual os dados ou o anexo pertencem.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param descricao Descrição detalhada do registro.
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     */
    public Manutencao(Condominio condominio, Usuario criadoPor, Unidade unidade, Area area,
                      Chamado chamado, String titulo, String descricao, TipoManutencao tipo) {
        this.condominio = condominio;
        this.criadoPor = criadoPor;
        this.unidade = unidade;
        this.area = area;
        this.chamado = chamado;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.status = StatusManutencao.AGENDADA;
    }
}