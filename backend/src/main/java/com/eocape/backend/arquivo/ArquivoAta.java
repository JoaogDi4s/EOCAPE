package com.eocape.backend.arquivo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.eocape.backend.ata.Ata;

import java.util.UUID;

@Entity
@Table(name = "arquivo_ata", schema = "eocape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArquivoAta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ata_id", nullable = false, updatable = false)
    private Ata ata;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arquivo_id", nullable = false, updatable = false)
    private Arquivo arquivo;

    public ArquivoAta(Ata ata, Arquivo arquivo) {
        this.ata = ata;
        this.arquivo = arquivo;
    }
}