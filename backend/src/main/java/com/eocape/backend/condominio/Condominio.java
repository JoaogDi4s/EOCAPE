package com.eocape.backend.condominio;
import com.eocape.backend.condominio.dto.CondominioRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa condomínios e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "condominio", schema = "eocape")
@Getter
@NoArgsConstructor
public class Condominio {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nome usado para identificar o registro.
     */
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Endereço do condomínio.
     */
    @Column(nullable = false, length = 100)
    private String endereco;

    /**
     * Cidade do condomínio.
     */
    @Column(nullable = false, length = 100)
    private String cidade;

    /**
     * Sigla da unidade federativa do condomínio.
     */
    @Column(nullable = false, length = 2)
    private String uf;

    /**
     * Indica se o registro está ativo para uso no sistema.
     */
    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return entidade de condominio localizada ou persistida pela operação.
     */
    public static Condominio from(CondominioRequest request) {
        Condominio condominio = new Condominio();
        condominio.nome = request.nome();
        condominio.endereco = request.endereco();
        condominio.cidade = request.cidade();
        condominio.uf = request.uf();
        return condominio;
    }
}