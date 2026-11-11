package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoAtaRepository extends JpaRepository<ArquivoAta, UUID> {

    List<ArquivoAta> findByAtaId(UUID ataId);

    boolean existsByAtaIdAndArquivoId(UUID ataId, UUID arquivoId);
}