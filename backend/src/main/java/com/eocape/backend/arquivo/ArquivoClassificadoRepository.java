package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoClassificadoRepository extends JpaRepository<ArquivoClassificado, UUID> {

    List<ArquivoClassificado> findByClassificadoId(UUID classificadoId);

    boolean existsByClassificadoIdAndArquivoId(UUID classificadoId, UUID arquivoId);
}