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

@Entity
@Table(name = "usuario", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O CPF é obrigatório")
    @Cpf
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O e-mail não pode ter mais de 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false, length = 15)
    private String telefone;

    // Nulo até o morador definir a senha na ativação
    @Column(name = "senha_hash")
    private String senhaHash;

    @Column(name = "primeiro_acesso_concluido", nullable = false)
    private boolean primeiroAcessoConcluido = false;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Usuario(String cpf, String nome, String email, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public void concluirPrimeiroAcesso(String novoSenhaHash) {
        this.senhaHash = novoSenhaHash;
        this.primeiroAcessoConcluido = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void definirSenhaInicial(String hash) {
        this.senhaHash = hash;
    }

    public void alterarSenha(String hash) {
        this.senhaHash = hash;
    }

    public void atualizarEmail(String novoEmail) {
        this.email = novoEmail;
    }
}