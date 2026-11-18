package com.eocape.backend.verificacaoEmail;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.eocape.backend.usuario.Usuario;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa verificações de e-mail e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "verificacao_email", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificacaoEmail {

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
     * Endereço de e-mail do usuário.
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Hash do código de verificação, sem armazenar o código em texto simples.
     */
    @Column(name = "codigo_hash", nullable = false)
    private String codigoHash;

    /**
     * Instante a partir do qual o código deixa de ser válido.
     */
    @Column(name = "expira_em", nullable = false, updatable = false)
    private Instant expiraEm;

    /**
     * Instante em que o código foi consumido, quando utilizado.
     */
    @Column(name = "usado_em")
    private Instant usadoEm;

    /**
     * Quantidade de tentativas de verificação já registradas.
     */
    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    /**
     * Inicializa VerificacaoEmail com os dados e as dependências necessários à operação.
     *
     * @param usuario Usuário associado ao registro.
     * @param email Endereço de e-mail do usuário.
     * @param codigoHash Hash do código de verificação, sem armazenar o código em texto simples.
     * @param validade Prazo de validade aplicado ao código ou token.
     */
    public VerificacaoEmail(Usuario usuario, String email, String codigoHash, Duration validade) {
        this.usuario = usuario;
        this.email = email;
        this.codigoHash = codigoHash;
        this.expiraEm = Instant.now().plus(validade);
    }

    /**
     * Incrementa a contagem de tentativas de verificação do código.
     */
    public void registrarTentativa() {
        this.tentativas++;
    }

    /**
     * Registra o consumo do código para impedir sua reutilização.
     */
    public void marcarComoUsado() {
        this.usadoEm = Instant.now();
    }

    /**
     * Verifica se o prazo de validade do código já foi encerrado.
     *
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    public boolean estaExpirada() {
        return Instant.now().isAfter(expiraEm);
    }

    /**
     * Verifica se o código de verificação já foi consumido.
     *
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    public boolean foiUsada() {
        return usadoEm != null;
    }
}