package com.eocape.backend.classificado;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa classificados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "classificado", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Classificado {

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
     * Usuário associado ao registro.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

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
    private TipoClassificado tipo;

    /**
     * Preço do anúncio; venda exige valor e doação não permite preço.
     */
    @Column(name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

    /**
     * Estado atual do registro no seu fluxo de atendimento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusClassificado status;

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
     * Inicializa Classificado com os dados e as dependências necessários à operação.
     *
     * @param condominio Condomínio que delimita o escopo deste registro.
     * @param usuario Usuário associado ao registro.
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     * @param descricao Descrição detalhada do registro.
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
     */
    public Classificado(Condominio condominio, Usuario usuario, String titulo, String descricao,
                        TipoClassificado tipo, BigDecimal preco) {
        this.condominio = condominio;
        this.usuario = usuario;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.preco = preco;
        this.status = StatusClassificado.ATIVO;
    }

    /**
     * Executa alterar titulo no fluxo de classificados.
     *
     * @param titulo Título apresentado na listagem e nos detalhes do registro.
     */
    public void alterarTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Executa alterar descricao no fluxo de classificados.
     *
     * @param descricao Descrição detalhada do registro.
     */
    public void alterarDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Executa alterar tipo epreco no fluxo de classificados.
     *
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
     */
    public void alterarTipoEPreco(TipoClassificado tipo, BigDecimal preco) {
        this.tipo = tipo;
        this.preco = preco;
    }

    /**
     * Altera o status de classificados.
     *
     * @param novoStatus Estado que será aplicado ao registro.
     */
    public void alterarStatus(StatusClassificado novoStatus) {
        this.status = novoStatus;
    }
}