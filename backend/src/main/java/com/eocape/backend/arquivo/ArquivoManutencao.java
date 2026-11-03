package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.manutencao.Manutencao;

import java.util.UUID;

@Entity
@Table(name = "arquivo_manutencao", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manutencao_id", nullable = false, updatable = false)
    private Manutencao manutencao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    public ArquivoManutencao(Manutencao manutencao, Arquivo arquivo) {
        this.manutencao = manutencao;
        this.arquivo = arquivo;
    }
}