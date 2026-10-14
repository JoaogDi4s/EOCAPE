package com.eocape.backend.condominio;
import com.eocape.backend.condominio.dto.CondominioRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "condominio", schema = "eocape")
@Getter
@NoArgsConstructor
public class Condominio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public static Condominio from(CondominioRequest request) {
        Condominio condominio = new Condominio();
        condominio.nome = request.nome();
        condominio.endereco = request.endereco();
        condominio.cidade = request.cidade();
        condominio.uf = request.uf();
        return condominio;
    }
}