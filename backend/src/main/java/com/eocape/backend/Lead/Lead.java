package com.eocape.backend.lead;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

/**
 * Representa contatos interessados e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lead {

    /**
     * Identificador único do registro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nome usado para identificar o registro.
     */
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Endereço de e-mail do usuário.
     */
    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "O email não pode ter mais de 100 caracteres")
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Número de telefone de contato.
     */
    @Pattern(regexp = "^[0-9]{10,15}$", message = "O telefone deve ter entre 10 e 15 dígitos")
    @Column(nullable = true, length = 15)
    private String telefone;

    /**
     * Nome do condomínio informado no contato.
     */
    @Size(max = 100, message = "O nome do condomínio não pode ter mais de 100 caracteres")
    @Column(name = "nome_condominio", nullable = true, length = 100)
    private String nomeCondominio;

    /**
     * Perfil informado pelo interessado no formulário de contato.
     */
    @NotNull(message = "O perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerfilLead perfil;

    /**
     * Indica se o interessado autorizou o contato pela equipe.
     */
    @Column(name = "consentimento_contato", nullable = false)
    private boolean consentimentoContato;

    /**
     * Instante de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
