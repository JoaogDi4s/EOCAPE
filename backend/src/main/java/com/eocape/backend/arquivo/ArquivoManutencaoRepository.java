package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoManutencaoRepository extends JpaRepository<ArquivoManutencao, UUID> {

    List<ArquivoManutencao> findByManutencaoId(UUID manutencaoId);

    boolean existsByManutencaoIdAndArquivoId(UUID manutencaoId, UUID arquivoId);
}