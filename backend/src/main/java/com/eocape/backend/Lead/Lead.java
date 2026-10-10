package com.eocape.backend.Lead;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O email não pode estar em branco")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "E-mail inválido")
    @Size(max = 100, message = "O email não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "O telefone deve ter entre 10 e 15 dígitos")
    @Column(nullable = true, length = 15)
    private String telefone;

    @Size(max = 100, message = "O nome do condomínio não pode ter mais de 100 caracteres")
    @Column(name = "nome_condominio", nullable = true, length = 100)
    private String nome_condominio;

    @NotNull(message = "O perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerfilLead perfil;

    @Column(name = "consentimento_contato", nullable = false)
    private boolean consentimento_contato;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criado_em;
}
