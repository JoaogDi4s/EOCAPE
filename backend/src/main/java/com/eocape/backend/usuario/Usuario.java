package com.eocape.backend.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa usuários e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "usuario", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     */
    @NotBlank(message = "O CPF é obrigatório")
    @Cpf
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /**
     * Nome usado para identificar o registro.
     */
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Endereço de e-mail do usuário.
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail não pode ter mais de 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Número de telefone de contato.
     */
    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false, length = 15)
    private String telefone;

    // Nulo até o morador definir a senha na ativação
    /**
     * Hash da senha; não contém a senha em texto simples.
     */
    @Column(name = "senha_hash")
    private String senhaHash;

    /**
     * Indica se o usuário concluiu a ativação e definiu a própria senha.
     */
    @Column(name = "primeiro_acesso_concluido", nullable = false)
    private boolean primeiroAcessoConcluido = false;

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
     * Instante da última atualização do registro.
     */
    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    /**
     * Inicializa Usuario com os dados e as dependências necessários à operação.
     *
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @param nome Nome usado para identificar o registro.
     * @param email Endereço de e-mail do usuário.
     * @param telefone Número de telefone de contato.
     */
    public Usuario(String cpf, String nome, String email, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    /**
     * Armazena o hash da senha e marca o primeiro acesso como concluído.
     *
     * @param novoSenhaHash Hash da nova senha já codificada.
     */
    public void concluirPrimeiroAcesso(String novoSenhaHash) {
        this.senhaHash = novoSenhaHash;
        this.primeiroAcessoConcluido = true;
    }

    /**
     * Desativa usuários.
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Armazena o hash da senha inicial de acesso do usuário.
     *
     * @param hash Hash da senha já codificado pelo PasswordEncoder.
     */
    public void definirSenhaInicial(String hash) {
        this.senhaHash = hash;
    }

    /**
     * Substitui o hash da senha sem alterar os demais dados cadastrais.
     *
     * @param hash Hash da senha já codificado pelo PasswordEncoder.
     */
    public void alterarSenha(String hash) {
        this.senhaHash = hash;
    }

    /**
     * Atualiza o endereço de e-mail associado ao usuário.
     *
     * @param novoEmail Novo endereço de e-mail confirmado pelo fluxo de verificação.
     */
    public void atualizarEmail(String novoEmail) {
        this.email = novoEmail;
    }
}